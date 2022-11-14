plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

group = "tk.mallumo"
version = "1.0"

val toolkit by lazy {
    Toolkit.get(extensions = extensions.extraProperties)
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        @Suppress("UNUSED_VARIABLE") val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)

                api("tk.mallumo:navigation:${toolkit["version.navigation"]}")
            }
            kotlin.srcDirs("build/generated/ksp/common/commonMain/kotlin")
        }
        @Suppress("UNUSED_VARIABLE") val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api(compose.preview)
            }
        }
        @Suppress("UNUSED_VARIABLE") val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }

        }
    }
}

dependencies {
    println("config names:")
    configurations.forEach {
        println(it.name)
    }
    add("kspAndroid", project(":navigation-ksp"))
    add("kspDesktop", project(":navigation-ksp"))
}



android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    @Suppress("UnstableApiUsage")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }

    @Suppress("UnstableApiUsage")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    namespace = "tk.mallumo.common"
}

ksp {
    arg("child", "child0 child1 child2 child3")
    arg("commonSourcesOnly", "true")

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}
