plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

group = "tk.mallumo"
version = "1.0"


kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
       val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)

//                api("tk.mallumo:navigation-core:${Deps.version.navigation.core }")
                implementation(project(":navigation-core"))
            }
            kotlin.srcDirs("build/generated/ksp/common/commonMain/kotlin")
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api(compose.preview)
            }
        }
      val desktopMain by getting {
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

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    namespace = "tk.mallumo.common"
}

ksp {
    arg("child", "child0 child1 child2 child3")
    arg("commonSourcesOnly", "true")
    arg("material3", "false")

}

java.toolchain. languageVersion.set(JavaLanguageVersion.of(11))

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
