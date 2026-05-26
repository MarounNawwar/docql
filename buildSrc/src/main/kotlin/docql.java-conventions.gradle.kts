// Convention plugin applied to every plain Java leaf module
plugins {
    id("docql.common-conventions")
    id("docql.testing-conventions")
    id("docql.packaging-conventions")
}

val enableMaintenanceConventions = providers.gradleProperty("docql.enableMaintenanceConventions")
    .map(String::toBoolean)
    .orElse(false)

if (enableMaintenanceConventions.get()) {
    pluginManager.apply("docql.maintenance-conventions")
}
