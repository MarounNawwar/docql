# Architecture — docql

## Design Philosophy

docql is built around three principles:

1. **Ports and adaptors.** Every external dependency (database, storage, search engine, job engine) is hidden behind a plain Java interface in an `api` module. Concrete implementations are swappable by changing a single factory call.
2. **Framework at the edges.** Spring Boot lives in `:web:service`, `:app`, and backend implementation modules that expose conditional `@Configuration` classes. Domain contracts stay plain Java.
3. **Distributed ownership, centralised query.** Each team pushes their own docs; the registry aggregates them. The system is designed to scale horizontally by team count, not by central editorial effort.

---

## Module Dependency Graph

```
          ┌────────────────────────────────────────────────────┐
          │                     :app                           │
          │  (Spring Boot entry point, DocqlConfig wiring)     │
          └──────────────────┬─────────────────────────────────┘
                             │ depends on all modules below
          ┌──────────────────▼─────────────────────────────────┐
          │                :web:service                         │
          │  (REST controllers, MapStruct WebMapper)            │
          │  depends on → :backend:web:api                      │
          │               :backend:publish:api                  │
          │               :backend:discovery:api                │
          │               :backend:search:api                   │
          └──────────────────┬─────────────────────────────────┘
                             │
   ┌─────────────────────────┼──────────────────────────┐
   │                         │                          │
:backend:publish:impl  :backend:discovery:impl    :backend:search:lucene
   │                         │                          │
:backend:publish:api   :backend:discovery:api     :backend:search:api ─── :backend:search:factory
:backend:publish:factory :backend:discovery:factory      │
   │                         │                     :backend:core:api
   └────────────┬────────────┘
                │ all service impls depend on:
        ┌───────┴────────┬──────────────┬──────────────┐
   :backend:db:api  :backend:storage:api :backend:cje:api :backend:core:api
   :backend:db:factory :backend:storage:factory :backend:cje:factory
        │                │               │
   :backend:db:postgres :backend:storage:fs :backend:cje:local
```

> **Rule**: arrows point downward only. No upward or horizontal dependencies between modules at the same layer.

---

## Data Flows

### Publish Flow

```
Client (CI/CD or manual)
        │
        ▼
POST /packages
        │
        ▼
PublishController (web:service)
        │  maps DTO → PublishRequest via WebMapper
        ▼
PublishService.publish(request) (backend:publish:impl)
        │
        ├─ 1. validate(request)       — IllegalArgumentException on missing fields
        ├─ 2. Assign packageId        — UUID
        ├─ 3. StorageBackend.store()  — write each file blob to storage backend
        ├─ 4. DocFileRepository.saveAll()   — persist file metadata + content to DB
        ├─ 5. DocPackageRepository.save()   — persist package record to DB
        └─ 6. CjeEngine.submit(INDEX_PACKAGE job)
                  │
                  ▼
           LocalCjeEngine (backend:cje:local)
                  │  INDEX_PACKAGE handler:
                  ├─ load DocPackage from DocPackageRepository
                  ├─ load DocFiles from DocFileRepository
                  └─ SearchEngine.index(file, team, product, version) for each file
                             │
                             ▼
                      LuceneSearchEngine (backend:search:lucene)
                             — writes to Lucene index (FSDirectory)
```

### Discovery Flow (Browse)

```
Client (AI agent or human)
        │
        ▼
GET /packages                 — list all packages
GET /packages?team=X          — filter by team
GET /packages?tag=Y           — filter by tag
GET /packages/{team}/{product}/latest          — resolve latest version
GET /packages/{team}/{product}/{version}       — exact version
GET /packages/{team}/{product}/{version}/files — list files
GET /packages/{team}/{product}/{version}/files/{path} — read file
        │
        ▼
DiscoveryController (web:service)
        │
        ▼
DiscoveryService (backend:discovery:impl)
        │
        ▼
DocPackageRepository / DocFileRepository (backend:db:postgres)
```

### Search Flow (Full-text)

```
Client (AI agent or human)
        │
        ▼
GET /search?q={query}&team={team}&tags={tags}
        │
        ▼
SearchController (web:service)
        │
        ▼
SearchEngine.search(query, tags, team) (backend:search:lucene)
        │  Lucene MultiFieldQueryParser over title + content fields
        │  Optional BooleanQuery filter on team field
        ▼
List<SearchResult> — ranked, up to 50 results
```

### Retract Flow

```
Client
        │
        ▼
DELETE /packages/{team}/{product}/{version}
        │
        ▼
PublishService.retract(team, product, version) (backend:publish:impl)
        ├─ DocFileRepository.deleteByPackageId()
        ├─ StorageBackend.deleteByPrefix()
        ├─ DocPackageRepository.deleteById()
        └─ SearchEngine.deindex(packageId)   ← TODO: currently not called in retract
```

---

## Backend Swap Guide

Every external concern is hidden behind an interface. To replace a backend:

| Layer | Interface | Current Impl | Swap target examples |
|---|---|---|---|
| Database | `DocPackageRepository`, `DocFileRepository` | JPA/Postgres (stub) | Any JPA-compatible RDBMS |
| Storage | `StorageBackend` | `FilesystemStorageBackend` | S3, Azure Blob, Nexus raw |
| CJE | `CjeEngine` | `LocalCjeEngine` (synchronous) | Jenkins, GitHub Actions, Kafka consumer |
| Search | `SearchEngine` | `LuceneSearchEngine` (in-memory) | Lucene FSDirectory, Elasticsearch, OpenSearch |

Steps to swap (example: replace FS storage with S3):
1. Add `:backend:storage:s3` module with `S3StorageBackend implements StorageBackend`.
2. Add `S3StorageFactory implements StorageFactory`.
3. Add module-local Spring config guarded by `@ConditionalOnProperty(prefix = "docql.storage", name = "impl", havingValue = "s3")`.
4. Register in `settings.gradle.kts`.
5. Add dependency in `app/build.gradle.kts`.
6. Set `docql.storage.impl=s3`.

---

## Auth Architecture (Planned)

Authentication is not yet implemented. The planned model is:

- **AI agents**: Personal Access Tokens (PAT) sent as `Authorization: Bearer <token>` headers. Tokens are scoped to read-only or read-write per team.
- **Human / enterprise access**: OIDC/SSO integration. The token is validated against the organisation's identity provider. Team membership is derived from token claims.
- **Publishing**: write-scoped PAT or CI/CD service account token.
- **Reading**: read-scoped PAT or anonymous (configurable per deployment).

A Spring Security filter chain will sit at the `:web:impl` boundary. Domain and service layers stay auth-agnostic.

---

## Deployment Architecture

### Current (Development)
Docker Compose with:
- `docql-app` — the Spring Boot JAR
- `postgres` — Postgres instance
- Mounted volume for Lucene FSDirectory index and filesystem storage

### Planned (Production)
Kubernetes deployment with:
- `docql-app` Deployment (horizontally scalable if search index is externalised)
- Managed Postgres (RDS, Cloud SQL, etc.)
- Object storage backend (S3 / Azure Blob) replacing local FS
- External search index (Elasticsearch / OpenSearch) replacing embedded Lucene
- Ingress with TLS termination
- Secret management via Kubernetes Secrets or Vault

---

## Key Constraints

- `:core:api` has zero dependencies. It is the foundation everything else depends on.
- Controllers and service classes see only interfaces. Concrete implementation selection is done by module-local Spring configuration classes guarded by `docql.*.impl` properties.
- The `CjeEngine` intentionally decouples publishing from indexing. Publishing returns immediately after persisting. Indexing happens in the CJE handler. This allows future async execution without changing the publish contract.
- All domain identifiers (`packageId`) are UUIDs generated server-side. Clients never assign IDs.
- Storage keys follow the pattern `{team}/{product}/{version}/{relativePath}`.

