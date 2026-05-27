plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":cje:api"))
    implementation(project(":cje:factory"))
}
