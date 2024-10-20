enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://repo.repsy.io/mvn/mallumo/public")
    }
}

@Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.repsy.io/mvn/mallumo/public")
    }
}

rootProject.name = "navigation"

include(":navigation-core")
include(":navigation-ksp")
//
//include(":test:android")
include(":test:desktop")
include(":test:common")
//
//include(":test:single-android")
