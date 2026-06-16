plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:job:api"))
    implementation(project(":backend:job:factory"))

    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}
