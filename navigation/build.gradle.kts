plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

val toolkit by lazy {
    Toolkit.get(extensions = extensions.extraProperties)
}

group = "tk.mallumo"
version = toolkit["version.navigation"]

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    android {
        publishLibraryVariants("release")
        publishLibraryVariantsGroupedByFlavor = true
    }
    js(IR)
    sourceSets {
        @Suppress("UNUSED_VARIABLE") val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${toolkit["version.coroutines"]}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${toolkit["version.kotlin"]}")
            }
        }
        @Suppress("UNUSED_VARIABLE") val jsMain by getting{
            dependencies {
                api(compose.web.core)
            }
        }
        @Suppress("UNUSED_VARIABLE") val jvmMain by getting {
            dependencies {
                api(compose.desktop.linux_x64)
            }
        }
        @Suppress("UNUSED_VARIABLE") val androidMain by getting {
            dependencies {
//                implementation("androidx.core:core-ktx:1.9.0")
                implementation("androidx.activity:activity-compose:${toolkit["version.compose.android.activity"]}")
            }
        }
    }
}

@Suppress("UnstableApiUsage", "OldTargetApi")
android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        namespace = "tk.mallumo.navigation"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
        disable("TypographyFractions", "TypographyQuotes")
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

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


apply("../secure.gradle")
