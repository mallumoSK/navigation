plugins {
    kotlin("multiplatform") version Deps.version.kotlin apply false
    kotlin("jvm") version Deps.version.kotlin apply false
    kotlin("android") version Deps.version.kotlin apply false
    id("com.android.application") version Deps.version.agp apply false
    id("com.android.library") version Deps.version.agp apply false
    id("com.google.devtools.ksp") version Deps.version.ksp apply false
    id("org.jetbrains.compose") version Deps.version.compose.desktop apply false
}

allprojects {
    repositories {
        maven("${rootDir.absolutePath}/repository")
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.repsy.io/mvn/mallumo/public")
    }
}
