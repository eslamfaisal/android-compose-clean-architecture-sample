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
    }
}

rootProject.name = "BakingApp"

// App Module
include(":app")

// Core Modules
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:security")
include(":core:ui")

// Feature Modules
include(":features:login")
include(":features:home")
include(":features:recipe-details")
include(":features:cooking-timer")

// SDK Modules
include(":metrics-sdk")
