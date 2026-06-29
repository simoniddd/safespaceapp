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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SafeSpace"

include(":app")

// Core
include(":core:di")
include(":core:data")
include(":core:ui")
include(":core:presentation")

// Feature: Expenses
include(":feature:expenses:api")
include(":feature:expenses:impl")

// Feature: Hub
include(":feature:hub:api")
include(":feature:hub:impl")
