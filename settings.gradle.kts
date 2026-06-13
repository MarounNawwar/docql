rootProject.name = "docql"

// Core shared domain model
include(":backend:core:api")

// Database abstraction
include(":backend:db:api")
include(":backend:db:factory")
include(":backend:db:postgres")

// File storage abstraction
include(":backend:storage:api")
include(":backend:storage:factory")
include(":backend:storage:fs")

// CJE (Content/Job Engine) abstraction
include(":backend:cje:api")
include(":backend:cje:factory")
include(":backend:cje:local")

// Publishing domain
include(":backend:publish:api")
include(":backend:publish:factory")
include(":backend:publish:impl")

// Discovery domain
include(":backend:discovery:api")
include(":backend:discovery:factory")
include(":backend:discovery:impl")

// Search abstraction
include(":backend:search:api")
include(":backend:search:factory")
include(":backend:search:lucene")

// Web REST layer
include(":backend:web:api")
include(":web:service")

// Assembled Spring Boot application
include(":app")

