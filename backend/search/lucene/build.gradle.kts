plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:search:api"))
    implementation(project(":backend:search:factory"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.lucene.core)
    implementation(libs.lucene.queryparser)
    implementation(libs.lucene.analysis.common)
    implementation("org.slf4j:slf4j-api")
}
