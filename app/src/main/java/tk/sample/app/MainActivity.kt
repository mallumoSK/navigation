package tk.sample.app

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.setContent
import tk.mallumo.compose.navigation.*
import tk.mallumo.just.files.style.SampleTheme

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

        // direct call of args
        Text(text = "nav args CONTENT:  ${nav.args.getString("item")}")

        Button(onClick = {
            //open third frame with no arguments
            nav.navTo_ThirdFrameUI()
        }) {
            Text(text = "navigate to secnd frame")
        }
    }
}


@Composable
@ComposableNavNode
fun ThirdFrameUI() {
    Column {
        Text(text = "third frame")
    }
}