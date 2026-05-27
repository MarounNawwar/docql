# Copilot Instructions — docql

> Read this file first on every session. It is the authoritative context for the docql project.
> Deeper references live in `.github/docs/`.

---

## What is docql?

docql is an **AI-first distributed documentation registry**.

Each team in an organisation owns and publishes a versioned **doc package** — a structured bundle of Markdown (and other format) files describing their product. docql stores, indexes, and exposes those packages through a clean REST API so that **AI agents** (IDE assistants, chat agents, CI bots) can retrieve, search, and reason over live, team-maintained documentation at query time.

The goal is to replace the expensive model of training a single monolithic AI on all company knowledge, by instead giving AI agents a real-time, queryable documentation layer that each team keeps up to date themselves.

See `.github/docs/vision.md` for the full problem statement and long-term ambition.

---

## Module Map

```
:core:api          — Immutable domain records (DocFile, DocPackage, PublishRequest, SearchResult). No dependencies.
:db:api            — Repository interfaces (DocPackageRepository, DocFileRepository).
:db:factory        — DbFactory interface for DB backend selection.
:db:postgres       — JPA-backed Postgres implementation (current placeholder uses in-memory store).
:storage:api       — StorageBackend interface (store/retrieve/list/delete blobs).
:storage:factory   — StorageFactory interface.
:storage:fs        — Filesystem implementation of StorageBackend.
:cje:api           — CjeEngine + CjeJob + CjeJobResult + CjeJobStatus. Content/Job Engine abstraction.
:cje:factory       — CjeFactory interface.
:cje:local         — In-process synchronous CjeEngine; INDEX_PACKAGE handler must call SearchEngine.
:publish:api       — PublishService interface (publish, retract).
:publish:factory   — PublishFactory interface.
:publish:impl      — DefaultPublishService: validate → store blobs → persist metadata → submit CJE job.
:discovery:api     — DiscoveryService interface (list, find, readFile).
:discovery:factory — DiscoveryFactory interface.
:discovery:impl    — DefaultDiscoveryService backed by db repositories.
:search:api        — SearchEngine interface (index, deindex, search).
:search:factory    — SearchFactory interface.
:search:lucene     — Apache Lucene implementation (currently in-memory; needs FSDirectory for persistence).
:web:api           — DTOs (DocFileDto, DocPackageDto, PublishRequestDto, SearchResultDto).
:web:impl          — Spring REST controllers (PublishController, DiscoveryController, SearchController) + MapStruct WebMapper.
:app               — Spring Boot entry point. DocqlConfig wires all factory beans. The only place that knows about concrete implementations.
```

---

## Key Design Rules — Always Follow These

### Architecture
- **Domain modules are framework-free.** `:core:api`, `:db:api`, `:storage:api`, `:cje:api`, `:publish:api`, `:discovery:api`, `:search:api` must never import Spring, JPA, or any framework annotation. They are plain Java 21.
- **Spring annotations live only in `:web:impl` and `:app`.** Service implementations (`:publish:impl`, `:discovery:impl`, etc.) are plain Java classes wired by the `DocqlConfig` `@Configuration` class.
- **All wiring is in `DocqlConfig`.** No `@Service` / `@Component` auto-scan on domain beans. Constructor injection only.
- **The factory pattern is the extension seam.** To swap a backend (e.g. filesystem → S3, local CJE → Jenkins), create a new factory class and change one `@Bean` method in `DocqlConfig`. No other files should need to change.
- **`api` modules define contracts. `factory` modules define selection interfaces. Named-backend modules (`postgres`, `fs`, `lucene`, `local`) are concrete implementations.** Never add a concrete implementation directly to an `api` module.

### Testing
- **Always propose tests alongside every implementation.** Every new class or method must have a corresponding test class.
- **Unit tests**: pure Java, no Spring context, fast. Use JUnit Jupiter. Mock collaborators with standard Java stubs or Mockito.
- **Integration tests**: use the `integrationTest` source set wired by `docql.testing-conventions`. These may spin up infrastructure (Testcontainers, embedded servers).
- **Test classes mirror the production package structure.** A class in `docql.publish.impl` is tested in `docql.publish.impl` under the test source root.
- **Test naming**: `{ClassUnderTest}Test` for unit tests, `{Feature}IntegrationTest` for integration tests.

### Code Style
- Java 21 — use records, sealed interfaces, pattern matching, text blocks where appropriate.
- Immutable data preferred. Domain records (`DocFile`, `DocPackage`, etc.) are Java records.
- Method length: prefer small, focused methods. Extract private helpers rather than writing long methods.
- No `null` returns from public API methods. Use `Optional<T>` for absent values, empty collections for empty lists.
- Spotless + PMD are configured. Run `.\gradlew.bat qualityCheck` before suggesting a PR is ready.
- Conventional commits: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:`.

### Adding a New Backend (e.g., S3 storage)
1. Create a new sub-module under the relevant domain (e.g., `:storage:s3`).
2. Implement the `api` interface (`StorageBackend`).
3. Create a factory class implementing the `factory` interface (`StorageFactory`).
4. Register the module in `settings.gradle.kts`.
5. Add the dependency in `app/build.gradle.kts`.
6. Swap the `@Bean` in `DocqlConfig`. Done.

---

## Current Technology Stack

| Concern | Current Choice | Future Options |
|---|---|---|
| Language | Java 21 | — |
| Framework | Spring Boot 3.3 | — |
| Build | Gradle (Kotlin DSL), multi-module | — |
| Database | JPA / Postgres (stub: in-memory) | Any JPA-compatible RDBMS |
| Storage | Local filesystem | S3, Azure Blob, Nexus raw |
| Search | Apache Lucene 9.10 (in-memory) | Lucene FSDirectory, Elasticsearch, OpenSearch |
| CJE | Local in-process (synchronous) | Jenkins, GitHub Actions, async queue |
| Mapping | MapStruct | — |
| API Docs | springdoc-openapi (Swagger UI) | — |
| Quality | Spotless, PMD | — |
| Auth | Not yet implemented | SSO (OIDC), PAT tokens |
| Deployment | Docker Compose | Kubernetes |

---

## Open TODOs (Immediate Priority)

1. **Wire real `INDEX_PACKAGE` handler** — `LocalCjeFactory` returns a no-op. It must receive `SearchEngine`, `DocFileRepository`, and `DocPackageRepository` beans, look up files by `packageId`, and call `searchEngine.index(file, team, product, version)` for each one.
2. **Lucene FSDirectory** — `LuceneSearchEngine` uses `ByteBuffersDirectory` (lost on restart). Switch to `FSDirectory` backed by a configurable path (`docql.search.index-dir`).
3. **Real JPA repositories** — `PostgresDocPackageRepository` and `PostgresDocFileRepository` use `ConcurrentHashMap`. Replace with Spring Data JPA entities and repositories. Add Flyway for schema management.
4. **Integration tests** — Testcontainers Postgres + FSDirectory-backed Lucene; cover the full publish → discover → search round-trip.
5. **Authentication** — PAT-based access for AI agents; SSO (OIDC) for human/enterprise access.

---

## Deeper Documentation

- `.github/docs/vision.md` — Problem statement, target users, long-term ambition, differentiator.
- `.github/docs/architecture.md` — Module dependency graph, data flows, design constraints.
- `.github/docs/domains.md` — Per-domain deep-dive (purpose, interfaces, current impl, extension points).
- `.github/docs/doc-package-standard.md` — The spec for a conformant doc package (structure, formats, versioning, index template).
- `.github/docs/roadmap.md` — Phased plan, MVP definition, out-of-scope items.
- `.github/docs/conventions.md` — Coding conventions, module naming, how to extend, quality tooling.

