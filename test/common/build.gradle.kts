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

    androidTarget()

    sourceSets {
        commonMain{
            kotlin.srcDirs("build/generated/ksp/common/commonMain/kotlin")
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)

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
    println("config names:")
    configurations.forEach {
        println(it.name)
    }
    add("kspAndroid", project(":navigation-ksp"))
    add("kspJvm", project(":navigation-ksp"))
}



android {
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        namespace = "tk.mallumo.common"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.targetSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

ksp {
    arg("child", "child0 child1 child2 child3")
    arg("commonSourcesOnly", "true")
    arg("material3", "true")

}
