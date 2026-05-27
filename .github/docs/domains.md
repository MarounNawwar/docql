# Domain Reference — docql

Each domain in docql is a bounded context with its own `api` (contract), `factory` (selection), and named implementation module(s). This file documents each domain's purpose, key interfaces, current implementation state, and known extension points.

---

## `:core:api` — Shared Domain Model

**Purpose**: Defines the immutable data records shared across all domains. Has no dependencies on any other module, framework, or library.

**Key types**:

| Record | Fields | Description |
|---|---|---|
| `DocPackage` | `id`, `team`, `product`, `version`, `tags`, `publishedAt`, `indexFilePath` | A versioned documentation bundle published by one team. |
| `DocFile` | `packageId`, `path`, `title`, `content` | A single documentation file inside a package. |
| `PublishRequest` | `team`, `product`, `version`, `tags`, `indexFilePath`, `files` | The inbound payload for publishing a new package. |
| `SearchResult` | `packageId`, `team`, `product`, `version`, `path`, `title`, `snippet` | A ranked search hit returned by the search engine. |

**Rules**:
- All types are Java records — immutable by design.
- No framework annotations (no Jackson, no JPA, no Spring).
- If a field may be absent, use `Optional<T>`. Never return `null` from a public method.
- When adding a new domain concept, add it here first as a record, then build the surrounding domain.

---

## `:db:api` — Repository Contracts

**Purpose**: Defines the persistence interfaces used by service layers. Hides all database details behind simple Java interfaces.

**Key interfaces**:

### `DocPackageRepository`
```
save(DocPackage)                                          → DocPackage
findById(String id)                                       → Optional<DocPackage>
findLatestByTeamAndProduct(String team, String product)   → Optional<DocPackage>
findByTeamProductAndVersion(String, String, String)       → Optional<DocPackage>
findAll()                                                 → List<DocPackage>
findByTeam(String team)                                   → List<DocPackage>
findByTag(String tag)                                     → List<DocPackage>
deleteById(String id)                                     → void
```

### `DocFileRepository`
```
saveAll(List<DocFile>)                                    → void
findByPackageId(String packageId)                         → List<DocFile>
findByPackageIdAndPath(String packageId, String path)     → Optional<DocFile>
deleteByPackageId(String packageId)                       → void
```

**Rules**:
- No SQL, JPA, or JDBC imports in this module.
- All methods return `Optional` or empty collections — never `null`.

---

## `:db:factory` — DB Backend Selection

**Purpose**: Defines `DbFactory`, the interface that produces both repository instances.

```java
public interface DbFactory {
    DocPackageRepository packageRepository();
    DocFileRepository fileRepository();
}
```

`DocqlConfig` calls the factory once per application startup. The factory encapsulates all wiring of the underlying persistence layer.

---

## `:db:postgres` — JPA/Postgres Implementation

**Purpose**: Concrete implementation of `DocPackageRepository` and `DocFileRepository` backed by Postgres via Spring Data JPA.

**Current state**: Placeholder — both repositories use a `ConcurrentHashMap` in-memory store. All queries are implemented but data is lost on restart.

**Target state**:
- Spring Data JPA `@Entity` classes for `DocPackage` and `DocFile`.
- Spring Data `JpaRepository`-backed implementations.
- Flyway migrations for schema management.
- `PostgresDbFactory` receives a `DataSource` (or EntityManagerFactory) via constructor injection from `DocqlConfig`.

**Extension points**:
- To add a new DB backend (e.g., MySQL, MongoDB), create a new module (e.g., `:db:mongodb`), implement both repository interfaces, create a factory, and swap the `@Bean` in `DocqlConfig`.

---

## `:storage:api` — Storage Contract

**Purpose**: Defines `StorageBackend`, the interface for binary blob storage.

```
store(String key, InputStream content)   → void
retrieve(String key)                     → InputStream
list(String prefix)                      → List<String>
deleteByPrefix(String prefix)            → void
exists(String key)                       → boolean
```

Keys follow the pattern: `{team}/{product}/{version}/{relativePath}`.

**Rules**: No filesystem, S3, or cloud SDK imports in this module.

---

## `:storage:factory` — Storage Backend Selection

Defines `StorageFactory`:
```java
public interface StorageFactory {
    StorageBackend storageBackend();
}
```

---

## `:storage:fs` — Filesystem Implementation

**Purpose**: Stores and retrieves blobs as plain files on a local filesystem path.

**Current state**: Functional. The root path is configurable via `docql.storage.root` (defaults to `docql-storage`). Used for local development and Docker Compose.

**Extension points**:
- `:storage:s3` — S3/MinIO backed implementation.
- `:storage:nexus` — Nexus raw repository backed implementation.
- `:storage:azure` — Azure Blob Storage implementation.

All of the above follow the same pattern: implement `StorageBackend`, create a `StorageFactory`, register in `settings.gradle.kts`, add to `app/build.gradle.kts`, swap the `@Bean`.

---

## `:cje:api` — Content/Job Engine Contract

**Purpose**: Defines the abstraction over a content processing and job execution engine. Decouples the act of publishing from the act of indexing.

**Key types**:

| Type | Description |
|---|---|
| `CjeEngine` | Single method: `submit(CjeJob) → CjeJobResult` |
| `CjeJob` | `id` (UUID), `type` (e.g., `"INDEX_PACKAGE"`), `payload` (packageId) |
| `CjeJobResult` | `jobId`, `status` (CjeJobStatus), `message` |
| `CjeJobStatus` | Enum: `SUCCESS`, `FAILED`, `SKIPPED` |

**Registered job types**:

| Type | Description |
|---|---|
| `INDEX_PACKAGE` | Load all files for a packageId from the DB, call `SearchEngine.index()` for each. |
| `VALIDATE_PACKAGE` | Run structural validation rules on a package. (stub) |

**Rule**: `CjeEngine.submit()` is synchronous in the local implementation. The interface contract does not promise synchronous execution — callers must not depend on the job being complete when `submit()` returns.

---

## `:cje:factory` — CJE Backend Selection

Defines `CjeFactory`:
```java
public interface CjeFactory {
    CjeEngine cjeEngine();
}
```

---

## `:cje:local` — In-Process CJE Implementation

**Purpose**: Runs jobs synchronously in the same JVM process. Designed for local development and early PoC.

**Current state**: `LocalCjeFactory` returns a `LocalCjeEngine` with a handler map. The `INDEX_PACKAGE` handler is currently a stub (returns SUCCESS without calling `SearchEngine`). This is **the most important open TODO**.

**Target state**: `LocalCjeFactory` must accept `SearchEngine`, `DocPackageRepository`, and `DocFileRepository` via constructor, then wire the `INDEX_PACKAGE` handler to:
1. Look up the `DocPackage` by `packageId`.
2. Load all `DocFile`s via `DocFileRepository.findByPackageId()`.
3. Call `searchEngine.index(file, team, product, version)` for each file.

**Extension points**:
- `:cje:jenkins` — Submit jobs to a Jenkins pipeline via HTTP API.
- `:cje:github-actions` — Trigger a GitHub Actions workflow dispatch event.
- `:cje:async` — Dispatch jobs to an internal thread pool or message queue (Kafka, RabbitMQ) before returning.

---

## `:publish:api` — Publish Contract

**Purpose**: Defines the single entry point for teams to push documentation.

```java
public interface PublishService {
    DocPackage publish(PublishRequest request);
    void retract(String team, String product, String version);
}
```

`publish()` is the orchestrator: validate → store blobs → persist metadata → trigger CJE.  
`retract()` reverses the process: delete files from DB, delete blobs from storage, delete package record.

---

## `:publish:factory` + `:publish:impl`

**`DefaultPublishService`** is the only implementation. No alternative publish flow is anticipated — the extension seam here is at the collaborator level (swap the DB, storage, or CJE backends, not the publish service itself).

**Validation rules** (enforced at publish time):
- `team` is required and non-blank.
- `product` is required and non-blank.
- `version` is required and non-blank (SemVer recommended).
- At least one file must be included.
- `indexFilePath` must be set and match a file in the package.

**Known gap**: `retract()` does not call `SearchEngine.deindex()` yet. Add that call alongside `fileRepository.deleteByPackageId()`.

---

## `:discovery:api` — Discovery Contract

**Purpose**: Read-only browsing of the registry. Answers the questions: what exists? what is the latest version? what does this file say?

```
listAll()                                               → List<DocPackage>
listByTeam(String team)                                 → List<DocPackage>
listByTag(String tag)                                   → List<DocPackage>
findLatest(String team, String product)                 → Optional<DocPackage>
findVersion(String team, String product, String version) → Optional<DocPackage>
readIndex(String team, String product, String version)  → Optional<DocFile>
readFile(String team, String product, String version, String filePath) → Optional<DocFile>
listFiles(String team, String product, String version)  → List<DocFile>
```

**Design note**: Discovery is backed purely by the DB. It does not touch storage or the search index. File content is stored in the DB (in `DocFile.content`) so that discovery is a single-tier read.

---

## `:discovery:factory` + `:discovery:impl`

**`DefaultDiscoveryService`** delegates to `DocPackageRepository` and `DocFileRepository`. There is no alternative implementation planned — the extension seam is the DB backend.

---

## `:search:api` — Search Contract

**Purpose**: Full-text search across all indexed documentation.

```java
public interface SearchEngine {
    void index(DocFile file, String team, String product, String version);
    void deindex(String packageId);
    List<SearchResult> search(String query, List<String> tags, String team);
}
```

- `index()` — called by the `INDEX_PACKAGE` CJE handler after publish.
- `deindex()` — called on retract (currently missing from `DefaultPublishService.retract()`).
- `search()` — called by `SearchController` on every search request.

**Indexed fields**:
| Field | Type | Stored | Searchable |
|---|---|---|---|
| `packageId` | StringField | yes | exact match (for deindex) |
| `team` | StringField | yes | filter |
| `product` | StringField | yes | filter |
| `version` | StringField | yes | filter |
| `path` | StringField | yes | stored only |
| `title` | TextField | yes | full-text |
| `content` | TextField | no | full-text |

**Known gaps**:
- `tags` filter in `search()` is accepted but not applied to the Lucene query.
- Snippet / highlight support is not implemented (`SearchResult.snippet` is always `""`).

---

## `:search:factory` + `:search:lucene`

**`LuceneSearchEngine`** uses Apache Lucene 9.10 with a `StandardAnalyzer` and `MultiFieldQueryParser` across `title` and `content`.

**Current state**: Uses `ByteBuffersDirectory` (in-memory). Index is lost on restart.

**Target state**: Accept a `Path` via constructor. Use `FSDirectory.open(path)`. Path bound to `docql.search.index-dir` config property in `DocqlConfig`.

**Extension points**:
- `:search:elasticsearch` — Elasticsearch/OpenSearch implementation.
- `:search:opensearch` — AWS OpenSearch implementation for cloud deployments.

---

## `:web:api` — REST DTOs

**Purpose**: Data Transfer Objects used by the REST layer. Kept separate from domain records so that the API contract can evolve independently of the domain model.

| DTO | Maps from/to |
|---|---|
| `DocPackageDto` | `DocPackage` |
| `DocFileDto` | `DocFile` |
| `PublishRequestDto` | `PublishRequest` |
| `SearchResultDto` | `SearchResult` |

---

## `:web:impl` — REST Controllers

**Purpose**: Spring MVC controllers and MapStruct mapper. The only module that may carry `@RestController`, `@RequestMapping`, `@RequestBody`, etc.

| Controller | Endpoints |
|---|---|
| `PublishController` | `POST /packages`, `DELETE /packages/{team}/{product}/{version}` |
| `DiscoveryController` | `GET /packages`, `GET /packages/{team}/{product}/{version}`, `GET /packages/{team}/{product}/{version}/files`, `GET /packages/{team}/{product}/{version}/files/{path}` |
| `SearchController` | `GET /search?q=&team=&tags=` |

**`WebMapper`** (MapStruct) converts between domain records and DTOs. It is a `@Mapper(componentModel = "default")` instance — used as `WebMapper.INSTANCE` everywhere.

---

## `:app` — Spring Boot Application

**Purpose**: The assembled runnable application. The **only** module that may import concrete implementation classes (e.g., `PostgresDbFactory`, `LuceneSearchFactory`).

**`DocqlConfig`** is the single `@Configuration` class. Every service bean is constructed via its factory. No `@ComponentScan` of domain packages. Constructor injection throughout.

When adding any new backend or service:
1. Add the dependency to `app/build.gradle.kts`.
2. Add a `@Bean` method to `DocqlConfig` that instantiates the factory and calls the appropriate factory method.
3. No other file in `:app` should change.

