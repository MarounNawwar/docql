plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:storage:api"))
    implementation(project(":backend:storage:factory"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.autoconfigure)
    implementation("org.slf4j:slf4j-api")
}
