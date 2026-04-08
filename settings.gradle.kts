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
include(":data")
include(":domain")
include(":feature-card")
include(":feature-deck")
include(":feature-stats")
include(":core")
include(":core:data")
include(":core:presentation")
include(":core:domain")
include(":core:design-system")
include(":core:test")
include(":di")
