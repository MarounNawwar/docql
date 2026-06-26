import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    id("docql.spring-conventions")
}

dependencies {
    implementation(project(":backend:core:api"))
    implementation(project(":backend:web:api"))
    implementation(project(":backend:publish:api"))
    implementation(project(":backend:discovery:api"))
    implementation(project(":backend:search:api"))
    implementation(project(":backend:storage:api"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    // MapStruct (annotation processor already provided by java-conventions)
    implementation(libs.mapstruct)
}

testing {
    suites {
        named<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(project(":backend:core:api"))
                implementation(project(":backend:web:api"))
                implementation(project(":backend:publish:api"))
                implementation(project(":backend:discovery:api"))
                implementation(project(":backend:search:api"))
                implementation(project(":backend:storage:api"))
                implementation(libs.spring.boot.starter.web)
                implementation(libs.spring.boot.starter.test)
            }
        }
    }
}

