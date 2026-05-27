plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":search:api"))
    implementation(project(":search:factory"))
    implementation(libs.lucene.core)
    implementation(libs.lucene.queryparser)
    implementation(libs.lucene.analysis.common)
}
