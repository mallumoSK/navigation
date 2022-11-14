# navigation

## Navigation component for Jetpack Compose

#### After config changes project clean + build is required

```shell
#                 KOTLIN-KSP-COMPOSE-LIBRARY
version.ksp=1.7.20-1.0.8
version.navigation=1.7.20-1.0.8-4.0.0

version.kotlin=1.7.20
version.agp=7.2.0
version.compose.desktop=1.2.1
version.compose.android=1.3.1
version.compose.android.activity=1.6.1
version.compose.android.compiller=1.3.2



//Previous
navigation =      '1.6.10-1.0.2-1.1.0-3.1.0'
navigation_ksp =  '1.6.10-1.0.2-1.1.0-3.1.0'
kotlin_version =  '1.6.10'
compose_version = '1.1.0'
ksp_version =     '1.6.10-1.0.2'

```

## navigation: ![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/navigation/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fnavigation%2Fmaven-metadata.xml&style=for-the-badge "Version")

## navigation-ksp: ![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/navigation-ksp/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fnavigation-ksp%2Fmaven-metadata.xml&style=for-the-badge "Version")

## About

* fast
* automatic generate code for navigation based on KSP

## Example

- shared navigation, see
    - test/android
    - test/desktop
    - test/common/commonMain
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

import androidx . compose . material . MaterialTheme
    import androidx . compose . ui . window . Window
    import androidx . compose . ui . window . application
    import tk . mallumo . compose . navigation . *

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

1. add plugin (**build.gradle.kts**)

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
    implementation("tk.mallumo:navigation:${version_navigation}")
    ksp("tk.mallumo:navigation-ksp:${version_navigation}")
}
//...

```

2. add pluginManagement **On top** of file **settings.gradle.kts** :

```groovy
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://mallumo.jfrog.io/artifactory/gradle-dev-local")
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
        maven("https://mallumo.jfrog.io/artifactory/gradle-dev-local")
    }
}
```

3. JOB DONE :)
