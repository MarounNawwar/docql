plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":db:api"))
    implementation(project(":discovery:api"))
    implementation(project(":discovery:factory"))
}
