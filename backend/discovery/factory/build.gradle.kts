plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:discovery:api"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.autoconfigure)

    runtimeOnly(project(":backend:discovery:impl"))
}
