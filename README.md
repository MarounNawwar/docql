# docql

Multi-module Gradle project with convention plugins defined in `buildSrc`.

## Convention plugins

- `docql.common-conventions`: Java 21 baseline, Maven Central, archive naming, UTF-8 compiler settings.
- `docql.testing-conventions`: JUnit Jupiter unit test defaults + `integrationTest` suite wired into `check`.
- `docql.packaging-conventions`: reproducible archives, `sourcesJar`, `javadocJar`, and `mavenJava` publication.
- `docql.java-conventions`: composed convention for standard modules (common + testing + packaging).
- `docql.spring-conventions`: Spring Boot modules, consumes `docql.java-conventions`, configures Boot dependency BOM.
- `docql.maintenance-conventions`: Spotless + PMD quality tooling.

## Module naming conventions

- Keep shared contracts in `api` modules.
- Keep creation/selection logic in `factory` modules.
- Name concrete backend modules after the backend or technology itself when it is explicit, for example `postgres`, `fs`, `local`, or `lucene`.
- Use `impl` only when there is a single obvious implementation for a domain and there is no better technology-specific name.

## Maintenance conventions

Maintenance tasks are available on all Java/Spring modules.

Quality enforcement on `check` remains opt-in so existing modules do not start failing unexpectedly.

Make `check` enforce formatting/static analysis too:

```powershell
.\gradlew.bat check -P"docql.enforceQualityOnCheck=true"
```

Run quality checks explicitly:

```powershell
.\gradlew.bat qualityCheck
```

Apply formatting fixes:

```powershell
.\gradlew.bat spotlessApply
```


