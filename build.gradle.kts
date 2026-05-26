
group = "docql"
version = "1.0-SNAPSHOT"

subprojects {
	val modulePath = path.removePrefix(":")
	val groupPath = modulePath.substringBeforeLast(":", missingDelimiterValue = modulePath)
		.replace(':', '.')

	group = if (groupPath.isBlank()) {
		rootProject.group
	} else {
		"${rootProject.group}.$groupPath"
	}

	version = rootProject.version
}

