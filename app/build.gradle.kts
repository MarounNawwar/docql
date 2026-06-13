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
    implementation(project(":backend:db:api"))
    implementation(project(":backend:db:factory"))
    implementation(project(":backend:db:postgres"))

    // storage
    implementation(project(":backend:storage:api"))
    implementation(project(":backend:storage:factory"))
    implementation(project(":backend:storage:fs"))

    // cje
    implementation(project(":backend:cje:api"))
    implementation(project(":backend:cje:factory"))
    implementation(project(":backend:cje:local"))

    // publish
    implementation(project(":backend:publish:api"))
    implementation(project(":backend:publish:factory"))
    implementation(project(":backend:publish:impl"))

    // discovery
    implementation(project(":backend:discovery:api"))
    implementation(project(":backend:discovery:factory"))
    implementation(project(":backend:discovery:impl"))

    // search
    implementation(project(":backend:search:api"))
    implementation(project(":backend:search:factory"))
    implementation(project(":backend:search:lucene"))

    // web
    implementation(project(":backend:web:api"))
    implementation(project(":web:service"))

    implementation(libs.spring.boot.starter.web)

    // JPA runtime + Postgres driver + Flyway
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)
    implementation(libs.flyway.core)
}
