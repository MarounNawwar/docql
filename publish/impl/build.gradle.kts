plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":db:api"))
    implementation(project(":storage:api"))
    implementation(project(":cje:api"))
    implementation(project(":publish:api"))
    implementation(project(":publish:factory"))
}
