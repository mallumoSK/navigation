plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.compose.core)
    alias(libs.plugins.compose.kotlin)
}


group = "tk.mallumo"
version = "1.0"

dependencies {
    implementation(project(":test:common"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation(libs.kotlin.coroutines)
    implementation(libs.androidx.compose.activity)

    implementation(compose.ui)
    implementation(compose.material)
    implementation(compose.preview)

    implementation(libs.me.log)
    implementation(project(":navigation-core"))

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.appcompat:appcompat:1.6.1")

    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "tk.mallumo.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "tk.mallumo.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
