plugins {
    id("docql.spring-conventions")
}

dependencies {
    implementation(project(":core:api"))
    implementation(project(":web:api"))
    implementation(project(":publish:api"))
    implementation(project(":discovery:api"))
    implementation(project(":search:api"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
