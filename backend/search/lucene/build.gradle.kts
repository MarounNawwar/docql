plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:search:api"))
    implementation(project(":backend:search:factory"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation(libs.lucene.core)
    implementation(libs.lucene.queryparser)
    implementation(libs.lucene.analysis.common)
}
