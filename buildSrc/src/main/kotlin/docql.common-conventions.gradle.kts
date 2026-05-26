// Shared baseline for all Java modules in the multi-project build.
plugins {
    java
}

base {
    archivesName = project.path.removePrefix(":").replace(':', '-')
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

