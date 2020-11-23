# navigation
## Navigation component for Jetpack Compose

## navigation: ![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/navigation/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fnavigation%2Fmaven-metadata.xml&style=for-the-badge "Version")

## navigation-ksp: ![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/navigation-ksp/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fnavigation-ksp%2Fmaven-metadata.xml&style=for-the-badge "Version")

## About
* no reflection
* no kapt
* fast
* automatic generate code for navigation based on KSP

## Example

### Navigation root -> NavigationActivity
```kotlin
import tk.mallumo.compose.navigation.*
import tk.mallumo.compose.navigation.MenuFrameUI

class MainActivity : NavigationActivity() {

    // pass frame node, which will be opened on app first startup
    override fun startupNode(): Node = Node.MenuFrameUI //generated value

    // this is default implementation but you can change :)
    override fun startupArgs(): Bundle = intent.extras ?: Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                NavigationContent()  //generated method
            }
        }
    }
}
```

### UI Frame (main)
```kotlin
import tk.mallumo.compose.navigation.*

// you can use bundle as arguments
// BUT binding into object is safer way :)
data class ArgsMenuFrame(var valueX: String = "")

@Composable
@ComposableNavNode(ArgsMenuFrame::class) // declaration frame node + arguments
fun MenuFrameUI() {
    val nav = NavigationAmbient.current // navigation between frames
    val args = remember { // read arguments
        // map bundle args into data-class of ArgsMenuFrame
        nav.bundledArgs<ArgsMenuFrame>()
    }

    Column {
        Text(text = "valueX = ${args.valueX}")
        Button(onClick = {
            //open second frame by passing arguments
            nav.navTo_SecondFrameUI(ArgsSecondFrame("1"))
            //alternative options:
            // nav.navTo_SecondFrameUI() // no arguments
            // nav.navTo_SecondFrameUI(bundleOf("item" to "1")) // arguments as bundle
        }) {
            Text(text = "navigate to second frame")
        }
    }
}
```

### UI Frame (second)
```kotlin
import tk.mallumo.compose.navigation.*

data class ArgsSecondFrame(var item: String = "")

@Composable
@ComposableNavNode(ArgsSecondFrame::class)
fun SecondFrameUI() {
    val nav = NavigationAmbient.current
    val args = remember { // read arguments
        // map bundle args into data-class of ArgsSecondFrame
        nav.bundledArgs<ArgsSecondFrame>()
    }
    Column {
        // call of args by mapped object
        Text(text = "bundledArgs CONTENT:  ${args.item}")

        Spacer(modifier = Modifier.preferredSize(20.dp))

        // direct call of args
        Text(text = "nav args CONTENT:  ${nav.args.getString("item")}")

        Spacer(modifier = Modifier.preferredSize(20.dp))

        Button(onClick = {
            //up navigation
            nav.up()
        }) {
            Text(text = "up")
        }

        Spacer(modifier = Modifier.preferredSize(20.dp))

        Button(onClick = {
            //open third frame with no arguments
            //and clear stack (remove MenuFrameUI and SecondFrameUI)
            nav.navTo_ThirdFrameUI(clearTop = true)
        }) {
            Text(text = "navigate to third frame + clear top")
        }
    }
}
```

### UI Frame (third)
```kotlin
import tk.mallumo.compose.navigation.ComposableNavNode

@Composable
@ComposableNavNode
fun ThirdFrameUI() {
    Column {
        Text(text = "third frame")
    }
}
```


## How to implement

1. add plugin (**build.gradle**)
```groovy
plugins {
    id("kotlin-ksp") version "1.4.0-dev-experimental-20200914"
}
//...
android{
    //...
}
//...

apply from: 'https://raw.githubusercontent.com/mallumoSK/navigation/master/ksp-config.gradle'
apply from: 'https://raw.githubusercontent.com/mallumoSK/navigation/master/ksp-navigation.gradle'

dependencies {
    implementation "tk.mallumo:navigation:x.y.z"
    ksp "tk.mallumo:navigation-ksp:x.y.z"
}
```

2. add pluginResolutionStrategy On top of file **settings.gradle** add this:
```groovy
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if ("kotlin-ksp".equals(requested.id.id)) {
                useModule("org.jetbrains.kotlin:kotlin-ksp:${requested.version}")
            }
            if ("org.jetbrains.kotlin.kotlin-ksp".equals(requested.id.id)) {
                useModule("org.jetbrains.kotlin:kotlin-ksp:${requested.version}")
            }
            if ("org.jetbrains.kotlin.ksp".equals(requested.id.id)) {
                useModule("org.jetbrains.kotlin:kotlin-ksp:${requested.version}")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        maven {
            url = "https://dl.bintray.com/kotlin/kotlin-eap"
        }
        google()
    }
}
```

3. JOB DONE :)