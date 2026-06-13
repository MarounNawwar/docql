plugins {
    id("docql.spring-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:web:api"))
    implementation(project(":backend:publish:api"))
    implementation(project(":backend:discovery:api"))
    implementation(project(":backend:search:api"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
