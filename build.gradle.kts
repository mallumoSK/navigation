plugins {
//    kotlin("android") version Deps.version.kotlin apply false
    alias(libs.plugins.compose.core) apply false
    alias(libs.plugins.compose.kotlin) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.android.app) apply false
}
