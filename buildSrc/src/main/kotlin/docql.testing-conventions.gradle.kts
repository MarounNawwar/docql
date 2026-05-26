import org.gradle.api.plugins.jvm.JvmTestSuite

// Unit and integration testing defaults for all Java modules.
plugins {
    `jvm-test-suite`
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()

            dependencies {
                implementation(project())
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.named("test"))
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.named("integrationTest"))
}
