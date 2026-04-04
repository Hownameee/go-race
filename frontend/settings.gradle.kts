pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }
    }
}

rootProject.name = "GoRace"
include(":app")
include(":core:common")
include(":core:system")
include(":core:model")
include(":core:data")
include(":feature:posts")
include(":feature:tracking")
include(":core:service")
include(":core:map")
include(":core:network")
include(":feature:auth:register")
include(":feature:auth:login")
include(":feature:profile")
include(":core:notification")
include(":core:model:notification")
include(":feature:notification")
include(":core:navigation")
