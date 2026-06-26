plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:persistence:api"))
    implementation(project(":backend:storage:api"))
    implementation(project(":backend:job:api"))
    implementation(project(":backend:scan:api"))
    implementation(project(":backend:publish:api"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.autoconfigure)
    implementation("org.slf4j:slf4j-api")

    testImplementation(platform(libs.spring.boot.dependencies))
    testImplementation("org.assertj:assertj-core")
}
