// Convention plugin for Spring Boot modules (web:impl, app)
plugins {
    id("docql.java-conventions")
    id("org.springframework.boot")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}

