@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.*
import org.jetbrains.kotlin.gradle.dsl.*
import java.util.*


plugins {
//    alias(libs.plugins.compose.core)
    alias(libs.plugins.compose.kotlin)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.lib)
    id("maven-publish")
}

val current = libs.me.nav.core.get()

group = current.group
version = current.version!!

kotlin {
    jvmToolchain(17)

    jvm()

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
        publishLibraryVariantsGroupedByFlavor = true
    }
    js(IR)
    wasmJs()

    sourceSets {

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            api(libs.kotlin.coroutines)
            api(libs.kotlin.reflect)
        }

        val webMain by creating {
            dependsOn(commonMain.get())
        }
        jsMain{
            dependsOn(webMain)
        }
        wasmJsMain{
            dependsOn(webMain)
            dependencies {
                implementation(libs.kotlin.browser)
            }
        }
        androidMain.dependencies {
            api(libs.androidx.compose.activity)
        }
    }
}

android {
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        namespace = "${current.group}.${current.name.replace("-", ".")}"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.targetSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        disable += setOf("TypographyFractions", "TypographyQuotes")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = false
    }
}

publishing {
    val rName = propertiesLocal["repsy.name"]
    val rKey = propertiesLocal["repsy.key"]
    repositories {
        mavenLocal()
        maven {
            name = "repsy.io"
            url = uri("https://repo.repsy.io/mvn/${rName}/public")
            credentials {
                username = rName
                password = rKey
            }
        }
    }
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = current.group
//            artifactId = current.name
//            version = current.version
//            from(components["java"])
//        }
//    }
}


val Project.propertiesLocal: LocalProperties get() = LocalProperties.get(this)

class LocalProperties private constructor(private val project: Project) {
    val prop = Properties().apply {
        project.rootProject.file("local.properties").reader().use {
            load(it)
        }
    }

    companion object {
        private lateinit var instance: LocalProperties
        internal fun get(project: Project): LocalProperties {
            if (!::instance.isInitialized) {
                instance = LocalProperties(project)
            }
            return instance
        }
    }

    operator fun get(key: String): String? = prop[key] as? String
}
