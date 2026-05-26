// Code maintenance defaults: formatting and static analysis.
plugins {
    id("com.diffplug.spotless")
    pmd
}

val enforceQualityOnCheck = providers.gradleProperty("docql.enforceQualityOnCheck")
    .map(String::toBoolean)
    .orElse(false)

spotless {
    java {
        target("src/**/*.java")
        trimTrailingWhitespace()
        endWithNewline()
        removeUnusedImports()
    }

    format("misc") {
        target("*.md", ".gitignore", "**/*.yaml", "**/*.yml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

pmd {
    isConsoleOutput = true
    ruleSets = listOf(
        "category/java/bestpractices.xml",
        "category/java/errorprone.xml"
    )
}

tasks.withType<Pmd>().configureEach {
    // Keep PMD informative by default; can be tightened per module later.
    ignoreFailures = true
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs formatting and static-analysis checks configured by conventions."
    dependsOn("spotlessCheck", "pmdMain", "pmdTest")
}

tasks.named("check") {
    if (enforceQualityOnCheck.get()) {
        dependsOn("qualityCheck")
    }
}

