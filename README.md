# navigation

## Navigation component for Jetpack Compose

#### After config changes project clean + build is required

## navigation: ![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fmvn%2Fmallumo%2Fpublic%2Ftk%2Fmallumo%2Fnavigation-core%2Fmaven-metadata.xml)

## navigation-ksp: ![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fmvn%2Fmallumo%2Fpublic%2Ftk%2Fmallumo%2Fnavigation-ksp%2Fmaven-metadata.xml)

## About

* fast
* automatic generate code for navigation based on KSP

## Example

- shared navigation, see
    - test/common/commonMain
    - test/android
    - test/desktop
- standalone android
    - test/single-android

### simple usage

```kotlin
//ANDROID

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.material.*
import tk.mallumo.compose.navigation.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme(darkTheme = true) {
                //generated method
                NavigationRoot(Node.AppUI)
            }
        }
    }
}

// DESKTOP

import androidx.compose.material.MaterialTheme 
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import tk.mallumo.compose.navigation.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            //generated method
            NavigationRoot(Node.AppUI)
        }
    }
}

// COMMON

@ComposableNavNode
@Composable
fun AppUI() {
    Text("123")
}
```

```kotlin
// CHILD NAV. GRAPH

// in build.gradle.kts
// between child navigation graphs put space
ksp.arg("child", "child0 child1 child2 child3")
```

```kotlin
//GENERATE SHARED COMMON NAVIGATION TREE, (shared between platforms)

// in build.gradle.kts
ksp.arg("commonSourcesOnly", "true")
```

## How to implement

### 1. add plugin (**build.gradle.kts**)
#### 1.1. single target (android or jvm)
```kotlin
plugins {
    id("com.google.devtools.ksp")
}

android {
    sourceSets {
        getByName("debug") {
            java.srcDirs(
                "build/generated/ksp/debug/kotlin",
            )
        }
        getByName("release") {
            java.srcDirs(
                "build/generated/ksp/release/kotlin",
            )
        }
    }
}

dependencies {
    implementation("tk.mallumo:navigation:${version_navigation_core}")
    ksp("tk.mallumo:navigation-ksp:${version_navigation_ksp}")
}
//...
```
#### 1.2. multiplatform or JS
```kotlin
plugins {
    id("com.google.devtools.ksp")
}

kotlin {
    js(IR)
    sourceSets {
        val commonMain by getting {
            kotlin.srcDirs("build/generated/ksp/common/commonMain/kotlin")
        }
        val jsMain by getting
    }
}

dependencies {
    implementation("tk.mallumo:navigation:${version_navigation_core}")
}
//...
val kspCommonMainMetadata by configurations
// in case of JS use `kspJs` in case of jvm use `kspJvm`, ...
val kspJs by configurations

dependencies {
    // in case of JS use `kspJs` in case of jvm use `kspJvm`, ...
    kspJs("tk.mallumo:navigation-ksp:${version_navigation_ksp}")
    
    kspCommonMainMetadata("tk.mallumo:navigation-ksp:${version_navigation_ksp}")
}

ksp.apply {
    arg("commonSourcesOnly", "true")
}
```
### 2. add pluginManagement **On top** of file **settings.gradle.kts** :

```groovy
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.repsy.io/mvn/mallumo/public")
    }
    plugins {
        kotlin("multiplatform") version extra["version.kotlin"] as String
        kotlin("jvm") version extra["version.kotlin"] as String
        kotlin("android") version extra["version.kotlin"] as String
        id("com.android.application") version extra["version.agp"] as String
        id("com.android.library") version extra["version.agp"] as String
        id("com.google.devtools.ksp") version extra["version.ksp"] as String
        id("org.jetbrains.compose") version extra["version.compose.desktop"] as String
        id("com.android.application") version extra["version.agp"] as String
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("${rootDir.absolutePath}/repository")
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.repsy.io/mvn/mallumo/public")
    }
}
```

### 3. JOB DONE :)
