plugins {
    id("docql.spring-conventions")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}

dependencies {
    // core
    implementation(project(":core:api"))

    // db
    implementation(project(":db:api"))
    implementation(project(":db:factory"))
    implementation(project(":db:impl-postgres"))

    // storage
    implementation(project(":storage:api"))
    implementation(project(":storage:factory"))
    implementation(project(":storage:impl-fs"))

    // cje
    implementation(project(":cje:api"))
    implementation(project(":cje:factory"))
    implementation(project(":cje:impl-local"))

    // publish
    implementation(project(":publish:api"))
    implementation(project(":publish:factory"))
    implementation(project(":publish:impl"))

    // discovery
    implementation(project(":discovery:api"))
    implementation(project(":discovery:factory"))
    implementation(project(":discovery:impl"))

    // search
    implementation(project(":search:api"))
    implementation(project(":search:factory"))
    implementation(project(":search:impl-lucene"))

    // web
    implementation(project(":web:api"))
    implementation(project(":web:impl"))

    implementation("org.springframework.boot:spring-boot-starter-web")
}
