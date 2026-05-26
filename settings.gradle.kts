rootProject.name = "docql"

// Core shared domain model
include(":core:api")

// Database abstraction
include(":db:api")
include(":db:factory")
include(":db:impl-postgres")

// File storage abstraction
include(":storage:api")
include(":storage:factory")
include(":storage:impl-fs")

// CJE (Content/Job Engine) abstraction
include(":cje:api")
include(":cje:factory")
include(":cje:impl-local")

// Publishing domain
include(":publish:api")
include(":publish:factory")
include(":publish:impl")

// Discovery domain
include(":discovery:api")
include(":discovery:factory")
include(":discovery:impl")

// Search abstraction
include(":search:api")
include(":search:factory")
include(":search:impl-lucene")

// Web REST layer
include(":web:api")
include(":web:impl")

// Assembled Spring Boot application
include(":app")

