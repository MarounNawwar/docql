# Roadmap — docql

## Guiding Principle

Build a small, working thing first — then grow it. Every phase must produce a fully functional system at its close. No half-built features land in main.

---

## Phase 0 — Close the Core Gaps (Current)

The architecture is in place. The critical missing pieces are functionality gaps in the working code. This phase makes the system fully functional end-to-end for the first time.

### Goals
- A published package is reachable through all three access patterns: discover by browse, discover by search, read file content.
- Data survives application restarts.
- Every new class written has a unit test.

### Deliverables

| Item | Description |
|---|---|
| Wire `INDEX_PACKAGE` handler | `LocalCjeFactory` must receive `SearchEngine`, `DocFileRepository`, `DocPackageRepository` and use them to index files on publish. |
| Lucene `FSDirectory` | Switch `LuceneSearchEngine` from `ByteBuffersDirectory` to `FSDirectory`. Path bound to `docql.search.index-dir`. |
| Real JPA repositories | Replace `ConcurrentHashMap` stubs in `PostgresDocPackageRepository` and `PostgresDocFileRepository` with Spring Data JPA entities and repositories. |
| Flyway schema management | Add Flyway. Define initial migration for `doc_packages` and `doc_files` tables. |
| Fix `deindex` on retract | Call `searchEngine.deindex(packageId)` inside `DefaultPublishService.retract()`. |
| Fix tag filtering in search | Apply `tags` filter in `LuceneSearchEngine.search()` as Lucene `TermQuery` filters. |
| Unit tests | Every new class gets a corresponding `*Test` class. Mockito for collaborators. |
| Docker Compose baseline | `docker-compose.yml` with `docql-app`, `postgres`. Volumes for index dir and storage root. |

---

## Phase 1 — MVP: End-to-End Working System

A team can publish documentation via the API and an AI agent can retrieve it. The system runs in Docker Compose. This is the first version worth demoing.

### Goals
- Publish → search round-trip works reliably.
- Integration tests cover the full pipeline.
- The API is documented (Swagger UI accessible).
- Basic access control is in place.

### Deliverables

| Item | Description |
|---|---|
| Integration tests | Testcontainers Postgres + FSDirectory Lucene. Cover: publish, discover, search, retract. |
| PAT authentication | `Authorization: Bearer <token>` middleware. Read-only and read-write scopes. Tokens stored in DB. |
| OpenAPI / Swagger UI | Confirm springdoc-openapi is wired and all endpoints are documented with examples. |
| Snippet / highlight in search | Add Lucene Highlighter to `SearchResultDto.snippet`. |
| `GET /packages?tag=` | Expose tag filter on the discovery endpoint. |
| Structured error responses | Consistent `ProblemDetail` (RFC 7807) for all error cases. |
| `application.properties` documentation | Document all configurable properties: `docql.storage.root`, `docql.search.index-dir`, DB URL, etc. |
| Health endpoint | Spring Actuator `GET /actuator/health` with DB and index readiness indicators. |

---

## Phase 2 — Hardening and Adoption Readiness

The system is stable enough for real team adoption. Package format is validated. Publishing is automated.

### Goals
- Teams can onboard in under 30 minutes.
- Doc package format is validated at publish time.
- A client tool exists to simplify publishing from CI/CD.

### Deliverables

| Item | Description |
|---|---|
| Package format validation | Validate index file exists in files array, validate `indexFilePath` is a real file, validate Markdown structure. CJE `VALIDATE_PACKAGE` handler becomes real. |
| `docql-client` CLI skeleton | A CLI tool (or GitHub Action step) that reads a local folder and pushes it as a doc package. |
| SSO / OIDC integration | Enterprise identity provider integration. Token claims used to derive team membership and access scope. |
| Rate limiting | Per-team publish rate limiting to prevent accidental or malicious bulk publishing. |
| Audit log | Record publish, retract, and access events. Stored in DB. Queryable via admin endpoint. |
| API versioning | Add `/v1/` prefix to all endpoints. Prepare for breaking change management. |

---

## Phase 3 — Scale and Backend Flexibility

The system can support larger deployments, pluggable storage, and external search backends.

### Goals
- Storage and search can be swapped to cloud-native backends without code changes to the core.
- The system can be deployed on Kubernetes.

### Deliverables

| Item | Description |
|---|---|
| S3/object storage backend | `:storage:s3` module. Configuration-driven selection in `DocqlConfig`. |
| External search backend | `:search:elasticsearch` or `:search:opensearch` module. FSDirectory Lucene remains available for simpler deployments. |
| Async CJE | `INDEX_PACKAGE` dispatched to a thread pool or message queue rather than blocking the request thread. |
| Kubernetes manifests | `k8s/` folder with Deployment, Service, ConfigMap, Secret, Ingress resources. |
| Multi-version diff | `GET /packages/{team}/{product}/{v1}/diff/{v2}` — returns a structured diff between two package versions. |
| Jenkins / GitHub Actions CJE | `:job:jenkins` and/or `:job:github-actions` modules. Triggered after publish, result reported async. |

---

## Out of Scope (For Now)

The following are explicitly excluded to keep focus on a working, adoptable system:

- **Frontend / UI** — API-first. A UI may come later but is not on the roadmap.
- **Custom query language** — The name "docql" hints at it, but a DSL is out of scope. REST + full-text search is sufficient.
- **Fine-grained RBAC** — Team-level access is the granularity target. Per-file or per-version ACLs are deferred.
- **Real-time collaboration** — docql is a push/pull registry, not a collaborative editor.
- **Generated documentation** — docql stores and indexes docs. It does not generate them (Javadoc, OpenAPI generation are the team's responsibility).
- **Complex infrastructure** — Kafka, multi-region replication, and distributed search clusters are Phase 3+ concerns.

---

## Decision Log

| Date | Decision | Rationale |
|---|---|---|
| Initial | Java 21 + Spring Boot 3.3 | Strong ecosystem, LTS Java version, team familiarity. |
| Initial | Gradle multi-module | Clean separation of concerns; independent compilation and testing per module. |
| Initial | Apache Lucene (embedded) | Zero infrastructure for early development. Swap path to Elasticsearch is clear. |
| Initial | Factory pattern for backends | Ensures any backend swap is a single-line change in `DocqlConfig`. |
| Initial | No Spring in domain modules | Domain logic must be portable, testable without a container, and framework-agnostic. |
| Initial | TDD as default | Every class has a test. Catch regressions early. Copilot always proposes tests alongside implementation. |
| Initial | SemVer for versions | Enables `latest` resolution logic and enterprise compliance requirements. |
| Initial | API-first, no UI | Keeps scope tight. AI agents consume REST. A UI can be added later without changing the API. |

