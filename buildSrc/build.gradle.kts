plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:3.3.0")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}
