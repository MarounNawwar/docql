import org.gradle.api.publish.maven.MavenPublication

// Packaging defaults to produce reproducible, publishable Java artifacts.
plugins {
    java
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(project.path)
                description.set("Module ${project.path} from ${rootProject.name}")
            }
        }
    }
}
