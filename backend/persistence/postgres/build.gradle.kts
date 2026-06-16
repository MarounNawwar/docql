plugins {
    id("docql.java-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:persistence:api"))
    implementation(project(":backend:persistence:factory"))

    // Spring Data JPA (entities + repositories live here)
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    implementation(libs.spring.boot.starter.data.jpa)

    // MapStruct runtime (Mappers.getMapper)
    implementation(libs.mapstruct)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.core)
}
