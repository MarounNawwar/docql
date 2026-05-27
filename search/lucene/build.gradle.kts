plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":search:api"))
    implementation(project(":search:factory"))
    implementation("org.apache.lucene:lucene-core:9.10.0")
    implementation("org.apache.lucene:lucene-queryparser:9.10.0")
    implementation("org.apache.lucene:lucene-analysis-common:9.10.0")
}
