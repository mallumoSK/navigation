enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://repo.repsy.io/user42380946/maven")
        mavenLocal()
    }
}

@Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.repsy.io/user42380946/maven")
        mavenLocal()
    }
}

rootProject.name = "navigation"

include(":navigation-core")
include(":navigation-ksp")
//
include(":test:android")
include(":test:desktop")
include(":test:common")

include("test:www")