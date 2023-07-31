pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.repsy.io/mvn/mallumo/public")
    }
}

rootProject.name = "navigation"

include(":navigation")
include(":navigation-ksp")
//
//include(":test:android")
//include(":test:desktop")
//include(":test:common")
//////
//include(":test:single-android")
