import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.register

// Convention plugin for OpenAPI-generated Java API modules.
plugins {
    id("docql.java-conventions")
}

val openApiGeneratorCli = configurations.create("openApiGeneratorCli")

dependencies {
    add("openApiGeneratorCli", "org.openapitools:openapi-generator-cli:7.11.0")
}

val openApiOutputDir = "build/generated/openapi"
val openApiSpec = "src/main/resources/openapi/docql.yaml"

val generateOpenApi = tasks.register<JavaExec>("generateOpenApi") {
    classpath = openApiGeneratorCli
    mainClass.set("org.openapitools.codegen.OpenAPIGenerator")

    args(
        "generate",
        "-g", "spring",
        "-i", openApiSpec,
        "-o", openApiOutputDir,
        "--api-package", "docql.web.api",
        "--model-package", "docql.web.dto",
        "--invoker-package", "docql.web.generated",
        "--type-mappings", "DateTime=Instant",
        "--import-mappings", "Instant=java.time.Instant",
        "--additional-properties",
        "interfaceOnly=true,skipDefaultInterface=true,useSpringBoot3=true,useTags=true,useResponseEntity=false,openApiNullable=false,dateLibrary=java8,documentationProvider=none",
        "--global-property",
        "apis,models,supportingFiles=false,apiDocs=false,modelDocs=false"
    )
}

the<SourceSetContainer>().named("main") {
    java.srcDir("$openApiOutputDir/src/main/java")
}

tasks.named("compileJava") {
    dependsOn(generateOpenApi)
}








