# navigation

## Type-safe Navigation Component for Compose Multiplatform & Jetpack Compose

> **Note**: After modifying navigation annotations or Gradle configuration, a project clean + build is recommended.

| Artifact            | Latest Version                                                                                                                                                                                                                                                                |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **navigation-core** | [![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fuser42380946%2Fmaven%2Ftk%2Fmallumo%2Fnavigation-core%2Fmaven-metadata.xml)](https://repo.repsy.io/user42380946/maven/tk/mallumo/navigation-core/maven-metadata.xml) |
| **navigation-ksp**  | [![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fuser42380946%2Fmaven%2Ftk%2Fmallumo%2Fnavigation-ksp%2Fmaven-metadata.xml)](https://repo.repsy.io/user42380946/maven/tk/mallumo/navigation-ksp/maven-metadata.xml)   |

---

## Features

- **Fast & Lightweight**: Zero reflection at runtime, compile-time validation.
- **KSP-Powered Code Generation**: Automatic type-safe navigation node and graph generation.
- **Compose Multiplatform Support**: Supports **Android**, **Desktop (JVM)**, and **Web (JS & Wasm-JS)**.
- **Child / Nested Navigation Graphs**: Support for independent sub-graphs and multi-level routing.
- **Material 3 Ready**: Built for modern Compose UI architectures.

---

## Project Structure & Examples

- **Shared Multiplatform Navigation**:
  - [`test/common`](file:///home/mallumo/projects/GitHub/me/navigation/test/common) - Shared navigation definitions,
    view models, and UI nodes (`commonMain`)
  - [`test/android`](file:///home/mallumo/projects/GitHub/me/navigation/test/android) - Android application consuming
    shared navigation
  - [`test/desktop`](file:///home/mallumo/projects/GitHub/me/navigation/test/desktop) - Desktop application consuming
    shared navigation
  - [`test/www`](file:///home/mallumo/projects/GitHub/me/navigation/test/www) - Web application (Kotlin/JS & Wasm-JS)
- **Standalone Android Example**:
  - [`test/single-android`](file:///home/mallumo/projects/GitHub/me/navigation/test/single-android) - Android app using
    navigation directly in an Android module

---

## Usage Guide

### 1. Declare Navigation Nodes

Annotate your `@Composable` functions with `@ComposableNavNode`:

```kotlin
// COMMON CODE (commonMain)
package tk.mallumo.common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import tk.mallumo.compose.navigation.*

// Simple node
@ComposableNavNode
@Composable
fun AppUI() {
  Text("Hello World!")
}

// Node with arguments
data class ScreenArgs(var userId: String = "", var count: Int = 0)

@ComposableNavNode(ScreenArgs::class)
@Composable
fun DetailScreenUI() {
  // Access typed arguments via LocalNavigation or constructor
  Text("Detail Screen")
}
```

### 2. Embed Navigation in Platform Roots

#### Android (`MainActivity.kt`)

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import tk.mallumo.compose.navigation.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          MaterialTheme {
            // Generated root entry
                NavigationRoot(Node.AppUI)
            }
        }
    }
}
```

#### Desktop JVM (`Main.kt`)

```kotlin
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import tk.mallumo.compose.navigation.*

fun main() = application {
  Window(onCloseRequest = ::exitApplication, title = "Desktop App") {
        MaterialTheme {
            NavigationRoot(Node.AppUI)
        }
    }
}
```

#### Web JS / Wasm-JS
```kotlin
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import tk.mallumo.compose.navigation.*

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  CanvasBasedWindow(canvasElementId = "ComposeTarget") {
    NavigationRoot(Node.AppUI)
  }
}
```

### 3. KSP Compiler Options

Add arguments in your module's `build.gradle.kts`:

```kotlin
ksp {
  // Specify the source set where generated files belong ("common" or "web")
  arg("navSourceSet", "common")

  // Optional: declare space-separated child navigation graphs
  arg("child", "child0 child1 child2")
}
```

---

## Setup & Installation

### Step 1: Add Maven Repository

In your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven("https://repo.repsy.io/user42380946/maven")
  }
}
```

### Step 2: Configure Module Build Script

#### Kotlin Multiplatform (`build.gradle.kts`)

```kotlin
plugins {
  alias(libs.plugins.kotlin.multiplatform) // or id("org.jetbrains.kotlin.multiplatform")
  alias(libs.plugins.compose.core)         // or id("org.jetbrains.compose")
  alias(libs.plugins.compose.kotlin)       // or id("org.jetbrains.kotlin.plugin.compose")
  alias(libs.plugins.android.lib)          // id("com.android.kotlin.multiplatform.library")
  alias(libs.plugins.kotlin.ksp)           // id("com.google.devtools.ksp")
}

kotlin {
  jvmToolchain(17)

  jvm()

  android {
    namespace = "your.package.name"
    compileSdk = 37
    minSdk = 24

    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
  }

  js {
    browser()
  }
  wasmJs {
    browser()
  }

  sourceSets {
    commonMain {
      // Include generated navigation code directory
            kotlin.srcDirs("build/generated/ksp/common/commonMain/kotlin")

      dependencies {
        implementation("tk.mallumo:navigation-core:<version>")
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
      }
    }
  }
}

dependencies {
  add("kspCommonMainMetadata", "tk.mallumo:navigation-ksp:<version>")
}

ksp {
  arg("navSourceSet", "common")
}

// Gradle 9+ task dependency tracking
tasks.matching {
  it.name in listOf("compileKotlinJvm", "compileAndroidMain", "compileKotlinJs", "compileKotlinWasmJs")
}.configureEach {
  dependsOn("kspCommonMainKotlinMetadata")
}
```

#### Standalone Android Application (`build.gradle.kts`)

```kotlin
plugins {
  alias(libs.plugins.android.app)
  alias(libs.plugins.compose.core)
  alias(libs.plugins.compose.kotlin)
  alias(libs.plugins.kotlin.ksp)
}

android {
  namespace = "your.package.name"
  compileSdk = 37

  defaultConfig {
    applicationId = "your.package.name"
    minSdk = 24
    targetSdk = 37
  }

  buildFeatures {
    compose = true
    }
}

dependencies {
  implementation("tk.mallumo:navigation-core:<version>")
  ksp("tk.mallumo:navigation-ksp:<version>")
}
```
