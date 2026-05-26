# docql

Multi-module Gradle project with convention plugins defined in `buildSrc`.

## Convention plugins

- `docql.common-conventions`: Java 21 baseline, Maven Central, archive naming, UTF-8 compiler settings.
- `docql.testing-conventions`: JUnit Jupiter unit test defaults + `integrationTest` suite wired into `check`.
- `docql.packaging-conventions`: reproducible archives, `sourcesJar`, `javadocJar`, and `mavenJava` publication.
- `docql.java-conventions`: composed convention for standard modules (common + testing + packaging).
- `docql.spring-conventions`: Spring Boot modules, consumes `docql.java-conventions`, configures Boot dependency BOM.
- `docql.maintenance-conventions`: Spotless + PMD quality tooling (opt-in, see below).

## Maintenance conventions (opt-in)

Maintenance conventions are available but not auto-enabled to avoid breaking existing checks.

Enable them for all modules:

```powershell
.\gradlew.bat check -P"docql.enableMaintenanceConventions=true" -P"docql.enforceQualityOnCheck=true"
```

Run quality checks explicitly:

```powershell
.\gradlew.bat qualityCheck -P"docql.enableMaintenanceConventions=true"
```

Apply formatting fixes:

```powershell
.\gradlew.bat spotlessApply -P"docql.enableMaintenanceConventions=true"
```


