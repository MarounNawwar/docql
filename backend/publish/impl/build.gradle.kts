plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:db:api"))
    implementation(project(":backend:storage:api"))
    implementation(project(":backend:cje:api"))
    implementation(project(":backend:publish:api"))
    implementation(project(":backend:publish:factory"))

    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}
