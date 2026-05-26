plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":db:api"))
    implementation(project(":db:factory"))
}
