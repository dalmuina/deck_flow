pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DeckFlow"
include(":app")
include(":core")
include(":design-system")
include(":data")
include(":feature-card")
include(":feature-deck")
include(":domain")
include(":core-test")
include(":core-ui")
include(":feature-stats")
