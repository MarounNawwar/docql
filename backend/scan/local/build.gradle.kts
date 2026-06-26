plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:scan:api"))
    implementation(project(":backend:scan:factory"))

    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}

