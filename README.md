# navigation
## Navigation component for Jetpack Compose
```
kotlin_version = '1.4.21'
compose_version = '1.0.0-alpha10'
```

## navigation: ![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/navigation/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fnavigation%2Fmaven-metadata.xml&style=for-the-badge "Version")

## navigation-ksp: ![https://mallumo.jfrog.io/artifactory/gradle-dev-local/tk/mallumo/navigation-ksp/](https://img.shields.io/maven-metadata/v?color=%234caf50&metadataUrl=https%3A%2F%2Fmallumo.jfrog.io%2Fartifactory%2Fgradle-dev-local%2Ftk%2Fmallumo%2Fnavigation-ksp%2Fmaven-metadata.xml&style=for-the-badge "Version")

## About
* no reflection
* no kapt
* fast
* automatic generate code for navigation based on KSP

## Example

### Navigation root

```kotlin
import tk.mallumo.compose.navigation.*
import tk.mallumo.compose.navigation.MenuFrameUI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme(darkTheme = true) {
                //generated method
                NavigationContent(
                    startupNode = Node.MenuFrameUI, // startup node
                    startupArgs = intent.extras, // startup node arguments
                    animation = tween() // transition between "windows"
                )
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
class ArgsMenuFrame(var valueX: String = "")

@Composable
@ComposableNavNode(ArgsMenuFrame::class) // declaration frame node + arguments
fun MenuFrameUI() {
    val nav = AmbientNavigation.current // navigation between frames + arguments management
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

class ArgsSecondFrame(var item: String = "")

class SecondFrameVM : NavigationViewModel() {
    val anyItem = mutbleStateOf("123")
    override fun onCleared() {
        anyItem.value = ""
        log("onCleared when node released")
    }
}

@Composable
@ComposableNavNode(ArgsSecondFrame::class)
fun SecondFrameUI() {
    val nav = AmbientNavigation.current
    val vm = navigationViewModel<SecondFrameVM>()
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
        Text(text = "vm.anyItem:  ${vm.anyItem.value}")

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
    id("symbol-processing") version "1.4.20-dev-experimental-20201222"
}
//...
android{
    //...
}
//...


//these 2 files download to local machine (faster compilation)
//then include as:
//apply from: '../ksp-config.gradle'
//apply from: '../ksp-navigation.gradle'

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
            if ("symbol-processing".equals(requested.id.id)){
                useModule("com.google.devtools.ksp:symbol-processing:${requested.version}")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}
```

3. JOB DONE :)