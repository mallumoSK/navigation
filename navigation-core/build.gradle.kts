plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("maven-publish")
}


group = Deps.group
version = Deps.version.navigation.core

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    androidTarget {
        publishLibraryVariants("release")
        publishLibraryVariantsGroupedByFlavor = true
    }
    js(IR)
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(Deps.lib.coroutines)
                api(Deps.lib.reflect)
            }
        }
        val jsMain by getting
        val desktopMain by getting {
            dependencies {
                api(compose.desktop.linux_x64)
            }
        }
        val androidMain by getting {
            dependencies {
                api(Deps.lib.composeActivity)
            }
        }
    }
}

@Suppress("UnstableApiUsage", "OldTargetApi", "DEPRECATION")
android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        compileSdk = 31
        namespace = "${Deps.group}.navigation"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += setOf("TypographyFractions", "TypographyQuotes")
    }
    buildFeatures {
        buildConfig = false
    }
}

//compose {
//    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:${Deps.version.compose.android}")
//}


publishing {
    val rName = propertiesLocal["repsy.name"]
    val rKey = propertiesLocal["repsy.key"]
    repositories {
        maven {
            name = "repsy.io"
            url = uri("https://repo.repsy.io/mvn/${rName}/public")
            credentials {
                username = rName
                password = rKey
            }
        }
    }
}
