package tk.sample.app

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import tk.mallumo.compose.navigation.*
import tk.mallumo.just.files.style.SampleTheme
import kotlin.random.Random

class MainActivity : NavigationActivity() {

    override fun startupNode(): Node = Node.MenuFrameUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                NavigationContent()
            }
        }
    }
}

data class MenuFrameArgs(var valueX: String = "")

@Composable
@ComposableNavNode(MenuFrameArgs::class)
fun MenuFrameUI() {
    val nav = NavigationAmbient.current
    val newItemValue = remember { Random(50).nextInt(200, 300) }
    val args = remember { nav.bundledArgs<MenuFrameArgs>() }

    Column {
        Text(text = "valueX = ${args.valueX}")
        Button(onClick = { nav.navTo_SecondFrameUI(SecondFrameArgs("1")) }) {
            Text(text = "navigate to secnd frame")
        }
        Divider(Modifier.preferredSize(20.dp))
        Button(onClick = {
            args.valueX = newItemValue.toString()
            nav.navTo_SecondFrameUI(SecondFrameArgs("2"))
        }) {
            Text(text = "change valueX to '${newItemValue}' and navigate to second frame")
        }
    }
}

data class SecondFrameArgs(var item: String = "")

@Composable
@ComposableNavNode(SecondFrameArgs::class)
fun SecondFrameUI() {
    val nav = NavigationAmbient.current
    val args = remember { nav.bundledArgs<SecondFrameArgs>() }
    Column {
        Text(text = "bundledArgs CONTENT:  ${args.item}")
        Text(text = "nav args CONTENT:  ${nav.args.getString("item")}")
    }
}