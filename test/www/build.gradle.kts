plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.core)
    alias(libs.plugins.compose.kotlin)
}

group = "one.devsbox"
version = "unspecified"



kotlin {
    jvmToolchain(17)
    js {
        browser()
    }
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser()
    }
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)

            implementation(projects.navigationCore)
        }
        webMain{
            kotlin.srcDirs("build/generated/ksp/metadata/webMain/kotlin")

            dependencies {
                implementation(projects.navigationCore)
            }
        }

    }
}

dependencies {
//    println("config names:")
//    configurations.filter { it.name.lowercase().contains("ksp") }.forEach {
//        println(it.name)
//    }
    add("ksp", projects.navigationKsp)
}

ksp{
    arg("navSourceSet", "web")
}