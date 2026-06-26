plugins {
    id("docql.spring-conventions")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}

dependencies {
    // core
    implementation(project(":backend:core:api"))

    // db
    implementation(project(":backend:persistence:api"))
    implementation(project(":backend:persistence:factory"))
    implementation(project(":backend:persistence:postgres"))

    // storage
    implementation(project(":backend:storage:api"))
    implementation(project(":backend:storage:factory"))
    implementation(project(":backend:storage:fs"))

    // cje
    implementation(project(":backend:job:api"))
    implementation(project(":backend:job:factory"))
    implementation(project(":backend:job:local"))

    // publish
    implementation(project(":backend:publish:api"))
    implementation(project(":backend:publish:factory"))

    // discovery
    implementation(project(":backend:discovery:api"))
    implementation(project(":backend:discovery:factory"))

    // search
    implementation(project(":backend:search:api"))
    implementation(project(":backend:search:factory"))
    implementation(project(":backend:search:lucene"))

    // scan
    implementation(project(":backend:scan:api"))
    implementation(project(":backend:scan:factory"))
    implementation(project(":backend:scan:local"))

    // web
    implementation(project(":backend:web:api"))
    implementation(project(":web:service"))

    implementation(libs.spring.boot.starter.web)

    // JPA runtime + Postgres driver + Flyway
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
}
