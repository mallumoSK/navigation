import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.core)
    alias(libs.plugins.compose.kotlin)
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.ksp)
}

group = "tk.mallumo"
version = "1.0"


kotlin {
    jvmToolchain(17)

    jvm()

    android {
        namespace = "tk.mallumo.common"
        compileSdk = 37
        minSdk = libs.versions.android.minSdk.get().toInt()

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDirs("build/generated/ksp/common/commonMain/kotlin")
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.materialIconsExtended)

                implementation(project(":navigation-core"))
            }
        }

        androidMain.dependencies {
            api("androidx.appcompat:appcompat:1.6.1")
            api(compose.preview)
        }

        jvmMain.dependencies {
            api(compose.preview)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":navigation-ksp"))
}

ksp {
    arg("child", "child0 child1 child2 child3")
    arg("navSourceSet", "common")
}

tasks.matching {
    it.name in listOf("compileKotlinJvm", "compileAndroidMain", "kspKotlinJvm", "kspKotlinAndroid")
}.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
