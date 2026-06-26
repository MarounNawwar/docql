plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:scan:api"))
    implementation(project(":backend:scan:factory"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.autoconfigure)
}

