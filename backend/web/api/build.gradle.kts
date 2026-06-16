plugins {
    id("docql.openapi-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    compileOnly(libs.spring.boot.starter.web)
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
    compileOnly("io.swagger.core.v3:swagger-annotations-jakarta:2.2.21")
}

