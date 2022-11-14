plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
}

val toolkit by lazy {
    Toolkit.get(extensions = extensions.extraProperties)
}

android {
    namespace = "tk.mallumo.common"
    compileSdk = 33


    @Suppress("UnstableApiUsage")
    defaultConfig {
        applicationId = "tk.mallumo.common"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    @Suppress("UnstableApiUsage")
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.create("release") {
                keyAlias = "sampledata"
                keyPassword = "sampledata"
                storeFile = file("${projectDir.absolutePath}/../sampledata/store.keystore")
                storePassword = "sampledata"
            }
        }
    }

    @Suppress("UnstableApiUsage")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    @Suppress("UnstableApiUsage")
    sourceSets {
        getByName("debug") {
            java.srcDirs(
                "build/generated/ksp/debug/kotlin",
//                "../common/build/generated/ksp/android/androidDebug/kotlin",
                "../common/src/commonMain/kotlin"
            )
        }
        getByName("release") {
            java.srcDirs(
                "build/generated/ksp/release/kotlin",
//                "../common/build/generated/ksp/android/androidDebug/kotlin",
                "../common/src/commonMain/kotlin"
            )
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    @Suppress("UnstableApiUsage")
    buildFeatures {
        compose = true
    }

    @Suppress("UnstableApiUsage")
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    @Suppress("UnstableApiUsage")
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${toolkit["version.coroutines"]}")
    implementation("androidx.compose.ui:ui:${toolkit["version.compose.android"]}")
    implementation("androidx.compose.material:material:${toolkit["version.compose.android"]}")
//    implementation("androidx.compose.ui:ui-tooling-preview:${toolkit["version.compose"]}")
    implementation("androidx.activity:activity-compose:${toolkit["version.compose.android.activity"]}")

    implementation("tk.mallumo:log:${toolkit["version.log"]}")
    implementation("tk.mallumo:utils:${toolkit["version.utils"]}")

    implementation(project(":navigation"))
    ksp(project(":navigation-ksp"))

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${toolkit["version.compose.android"]}")
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.appcompat:appcompat:1.5.1")

    debugImplementation("androidx.compose.ui:ui-test-manifest:${toolkit["version.compose.android"]}")
}

ksp {
    arg("child", "child0")
}
