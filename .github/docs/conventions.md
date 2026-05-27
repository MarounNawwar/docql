# Conventions — docql

## Purpose

This document is the authoritative reference for how code is written, structured, and extended in docql. Every contributor (human or AI) must follow these conventions. When in doubt, check here before making a decision.

---

## Language and Runtime

- **Java 21** is the baseline. Use modern Java features where they improve clarity:
  - Records for immutable data (`DocFile`, `DocPackage`, etc.)
  - Sealed interfaces for exhaustive type hierarchies (e.g., job status, result types)
  - Pattern matching (`instanceof`, `switch` expressions)
  - Text blocks for multiline strings in tests or templates
  - `var` for local variables where the type is obvious from the right-hand side
- Avoid Java 8-era idioms when a cleaner Java 21 alternative exists.

---

## Module Naming

| Suffix | Purpose | Example |
|---|---|---|
| `:api` | Contracts only — interfaces and records. Zero dependencies. | `:publish:api`, `:search:api` |
| `:factory` | Selection interface for a backend. Produces an instance of the `api` interface. | `:db:factory`, `:cje:factory` |
| `:impl` | Single obvious implementation with no better technology-specific name. | `:publish:impl`, `:discovery:impl` |
| `:postgres`, `:lucene`, `:fs`, `:local` | Named-backend module. Technology is explicit. | `:db:postgres`, `:search:lucene` |

**Do not** name a concrete implementation `:impl` when there is a good technology-specific name. `:storage:s3` is better than `:storage:impl`.

### Package Naming

Java packages mirror the module path:

| Module | Package |
|---|---|
| `:core:api` | `docql.core` |
| `:db:api` | `docql.db` |
| `:db:postgres` | `docql.db.postgres` |
| `:publish:impl` | `docql.publish.impl` |
| `:search:lucene` | `docql.search.lucene` |
| `:web:impl` | `docql.web.controller`, `docql.web.mapper` |
| `:app` | `docql.app` |

---

## Architecture Conventions

### Layer Rules

| Layer | Spring allowed? | Framework allowed? | May see concrete impls? |
|---|---|---|---|
| `:core:api` | ❌ | ❌ | ❌ |
| `:db:api`, `:storage:api`, `:cje:api`, `:search:api`, `:publish:api`, `:discovery:api` | ❌ | ❌ | ❌ |
| `:db:factory`, `:storage:factory`, `:cje:factory`, `:search:factory` | ❌ | ❌ | ❌ |
| `:db:postgres`, `:storage:fs`, `:cje:local`, `:search:lucene` | ❌ Spring annotations | ✅ JPA, Lucene, etc. | ✅ own only |
| `:publish:impl`, `:discovery:impl` | ❌ | ❌ | ❌ |
| `:web:api` | ❌ | ❌ | ❌ |
| `:web:impl` | ✅ `@RestController`, `@RequestMapping` etc. | ✅ Jackson, MapStruct | ❌ (sees interfaces only) |
| `:app` | ✅ `@Configuration`, `@Bean` | ✅ | ✅ all |

### Wiring Rules

- **All beans are wired in `DocqlConfig`**. Do not add `@Service`, `@Component`, or `@Repository` to any class outside `:web:impl`.
- **Constructor injection only**. No field injection (`@Autowired` on fields). No setter injection.
- **`DocqlConfig` is the only class that imports concrete implementation classes**. Controllers and service classes receive interfaces.

### Returning Values

- **Never return `null` from a public method.** Use `Optional<T>` for values that may be absent.
- **Use empty collections**, never `null`, for list-returning methods.
- **Throw `IllegalArgumentException`** for invalid input detected at service boundaries (e.g., blank required field).
- **Throw `IllegalStateException`** for unexpected system conditions.
- Custom exceptions may be added as the domain grows, but keep them close to the domain that owns them.

---

## Test-Driven Development

**TDD is the default.** Every implementation must have a test. Copilot must always propose tests alongside any implementation it generates.

### Unit Tests

- Location: `src/test/java` in the same module as the class under test.
- Naming: `{ClassUnderTest}Test` — e.g., `DefaultPublishServiceTest`, `LuceneSearchEngineTest`.
- Framework: JUnit Jupiter (`@Test`, `@BeforeEach`, `@DisplayName`, `@ParameterizedTest`).
- Mocking: Mockito for collaborators. Standard Java stubs where simple.
- **No Spring context in unit tests.** Instantiate the class under test directly with mocked collaborators.
- Each test class covers one class. Test one behaviour per test method.

### Integration Tests

- Location: `src/integrationTest/java` (wired by `docql.testing-conventions`).
- Naming: `{Feature}IntegrationTest` — e.g., `PublishDiscoverIntegrationTest`, `SearchIntegrationTest`.
- Infrastructure: Testcontainers for Postgres. FSDirectory Lucene with a temp folder. No mocking of real backends.
- The golden integration test covers: publish a package → discover it by team → search for a term in its content → retract it → confirm it is gone.
- Run with: `.\gradlew.bat integrationTest`

### Test Data

- Keep test data inline. Do not rely on shared fixtures that cross test classes.
- Use builder-style helpers or static factory methods within the test class to keep test setup readable.
- Use `@TempDir` for filesystem paths in tests. Never hardcode file paths.

---

## Code Style

### Formatting

- Spotless is configured on all modules.
- Run `.\gradlew.bat spotlessApply` to auto-apply formatting.
- Run `.\gradlew.bat qualityCheck` to check formatting + PMD before raising a PR.
- Do not suppress PMD warnings without a comment explaining why.

### Method Design

- **Prefer small, focused methods.** A method does one thing.
- **Extract private helpers** rather than writing long methods. If a method exceeds ~20 lines, look for an extraction.
- **Name methods after what they do**, not how: `findLatest()` not `queryForMostRecentByTimestamp()`.
- Avoid boolean parameters (`doThing(true)` is unreadable). Use enums or separate methods.

### Immutability

- Domain records are records — they are inherently immutable. Never add setters.
- Collections passed into constructors should be defensively copied or made unmodifiable.
- Lists returned from repositories and services should be unmodifiable (`List.of()`, `List.copyOf()`, `.toList()`).

---

## Commit Conventions

Use [Conventional Commits](https://www.conventionalcommits.org/):

| Prefix | When to use |
|---|---|
| `feat:` | A new feature |
| `fix:` | A bug fix |
| `test:` | Adding or correcting tests |
| `refactor:` | Code change with no behaviour change |
| `docs:` | Documentation only |
| `chore:` | Build, tooling, dependency updates |
| `perf:` | Performance improvement |

Examples:
```
feat: wire INDEX_PACKAGE handler with real SearchEngine call
fix: call deindex on retract in DefaultPublishService
test: add LuceneSearchEngineTest covering tag filter
refactor: extract storageKey helper into StorageKeys utility class
chore: upgrade lucene to 9.11.0
```

---

## Adding a New Backend — Step by Step

This is the most common extension pattern. Follow it exactly.

**Example: Add S3 storage backend**

1. Create module `:storage:s3`:
   ```
   storage/
   └── s3/
       ├── build.gradle.kts     ← apply docql.java-conventions; depend on :storage:api
       └── src/main/java/
           └── docql/storage/s3/
               ├── S3StorageBackend.java    ← implements StorageBackend
               └── S3StorageFactory.java   ← implements StorageFactory
   ```

2. Register in `settings.gradle.kts`:
   ```kotlin
   include(":storage:s3")
   ```

3. Add to `app/build.gradle.kts`:
   ```kotlin
   implementation(project(":storage:s3"))
   ```

4. Swap the `@Bean` in `DocqlConfig`:
   ```java
   @Bean
   public StorageBackend storageBackend(@Value("${docql.storage.s3.bucket}") String bucket) {
       return new S3StorageFactory(bucket).storageBackend();
   }
   ```

5. Write unit tests for `S3StorageBackend` using a mock S3 client or LocalStack.

6. No other files change.

---

## Adding a New Domain Endpoint

When adding a new capability (e.g., a new query type, a new field to discovery):

1. **Add to the `api` interface** first. The interface is the contract.
2. **Add to the implementation** class (`impl` or named-backend module).
3. **Add to the DTO** in `:web:api` if the new field surfaces through the REST API.
4. **Add to `WebMapper`** if mapping is needed.
5. **Add to the controller** in `:web:impl`.
6. **Write tests** at each step: unit test the service impl, integration test the full round-trip.

---

## Quality Gates

Before any feature is considered done:

- [ ] Unit tests written and passing.
- [ ] Integration test updated if the feature touches the publish/discover/search pipeline.
- [ ] `.\gradlew.bat qualityCheck` passes (Spotless + PMD).
- [ ] No `null` returns added to any public API method.
- [ ] No Spring annotations added outside `:web:impl` and `:app`.
- [ ] `DocqlConfig` is the only class that references a concrete implementation class.
- [ ] Commit message follows Conventional Commits format.

