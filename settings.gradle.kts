rootProject.name = "docql"

// Core shared domain model
include(":backend:core:api")

// Persistence abstraction
include(":backend:persistence:api")
include(":backend:persistence:factory")
include(":backend:persistence:postgres")

// File storage abstraction
include(":backend:storage:api")
include(":backend:storage:factory")
include(":backend:storage:fs")

// Job Engine (CJE) abstraction
include(":backend:job:api")
include(":backend:job:factory")
include(":backend:job:local")

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

// Malware scan abstraction
include(":backend:scan:api")
include(":backend:scan:factory")
include(":backend:scan:local")

// Web REST layer
include(":backend:web:api")
include(":web:service")

// Assembled Spring Boot application
include(":app")

