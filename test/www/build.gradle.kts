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
        webMain.dependencies {
            implementation(projects.navigationCore)
        }

    }
}

dependencies {
    println("config names:")
    configurations.filter { it.name.lowercase().contains("ksp") }.forEach {
        println(it.name)
    }
    kspCommonMainMetadata(projects.navigationKsp)
//    ksp(projects.navigation)
    add("kspJs", projects.navigationKsp)

//    add(ksp<webMain>, projects.navigation)
//    add("kspWebMainMetadata", projects.navigation)
//    kspJs(catalog.me.nav.ksp)
}
