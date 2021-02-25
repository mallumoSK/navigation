package tk.sample.app

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tk.mallumo.compose.navigation.*
import tk.mallumo.just.files.style.SampleTheme
import tk.mallumo.just.files.style.SampleThemePreview
import tk.mallumo.log.log
import kotlin.collections.contains

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleTheme(darkTheme = true) {
                //generated method
                NavigationContent(
                    startupNode = Node.MenuFrameUI,
                    startupArgs = intent.extras,
                    animation = tween()
                )
            }
        }
    }
}

// you can use bundle as arguments
// BUT binding into object is safer way :)
data class ArgsMenuFrame(var valueX: String = "")

//@Composable
//@ComposableNavNode
//fun XFrameUI() {
//
//}

@Composable
@ComposableNavNode(ArgsMenuFrame::class) // declaration frame node + arguments
fun MenuFrameUI() {
    val nav = LocalNavigation.current // navigation between frames

    val args = remember { // read arguments
        // map bundle args into data-class of ArgsMenuFrame
        nav.bundledArgs<ArgsMenuFrame>()
    }

    Column {
        Text(text = "valueX = ${args.valueX}")
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            //open second frame by passing arguments
            nav.navTo_SecondFrameUI(ArgsSecondFrame("1"))
            //alternative options:
            // nav.navTo_SecondFrameUI() // no arguments
            // nav.navTo_SecondFrameUI(bundleOf("item" to "1")) // arguments as bundle
        }) {
            Text(text = "navigate to second frame")
        }

//        val (textVal,g) by  remember { mutableStateOf("")  }
        val textVal = remember { mutableStateOf(TextFieldValue()) }

        TextField(
            value = textVal.value,
            onValueChange = { textVal.value = it },
            modifier = Modifier.externalKeyboard(textVal)
        )
        Spacer(modifier = Modifier.size(16.dp))
    }
}


data class ArgsSecondFrame(var item: String = "", var item2: String = "",  var item25: String = "")


class SecondFrameVM : NavigationViewModel() {
    init {
        log("init SecondFrameVM")
    }

    override fun onCleared() {
        log("onCleared SecondFrameVM")
    }

}

@Composable
@ComposableNavNode(ArgsSecondFrame::class)
fun SecondFrameUI() {
    val nav = LocalNavigation.current
    val vm = navigationViewModel<SecondFrameVM>()
    val args = remember { // read arguments
        // map bundle args into data-class of ArgsSecondFrame
        nav.bundledArgs<ArgsSecondFrame>()
    }
    Column {
        // call of args by mapped object
        Text(text = "bundledArgs CONTENT:  ${args.item}")

        Spacer(modifier = Modifier.size(20.dp))

        // direct call of args
        Text(text = "nav args CONTENT:  ${nav.args.getString("item")}")

        Spacer(modifier = Modifier.size(20.dp))

        Button(onClick = {
            //up navigation
            nav.up()
        }) {
            Text(text = "up")
        }

        Spacer(modifier = Modifier.size(20.dp))

        Button(onClick = {
            //open third frame with no arguments
            //and clear stack (remove MenuFrameUI and SecondFrameUI)
            nav.navTo_ThirdFrameUI(clearTop = true)
        }) {
            Text(text = "navigate to third frame + clear top")
        }
    }
}

class ThirdFrameVM : NavigationViewModel() {
    val itemText = mutableStateOf("")
    override fun onCleared() {
        log("CLEAN-UP")
        itemText.value = ""
    }

    fun click() {
        val (getter, setter) = itemText
        setter(
            if (getter.isEmpty()) ": on click "
            else "$getter."
        )
    }

}

@Composable
@ComposableNavNode
fun ThirdFrameUI() {
    val vm = navigationViewModel<ThirdFrameVM>()
    Column(modifier = Modifier.clickable { vm.click() }) {
        Text(
            text = "third frame${vm.itemText.value}",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewThirdFrameUI() {
    SampleThemePreview {
        ThirdFrameUI()
    }
}


//jryyjfr
fun Modifier.externalKeyboard(input: MutableState<TextFieldValue>): Modifier {
    return onKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown
            || event.nativeKeyEvent.keyCode in arrayOf(
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT
            )
        ) {
            log(event.nativeKeyEvent.keyCode)
            var selection = input.value.selection
            var code = event.nativeKeyEvent.unicodeChar.toChar().toString()
            val newSelection: TextRange
            when (event.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    code = ""
                    newSelection = if (selection.start == 0) {
                        TextRange(selection.start)
                    } else {
                        TextRange(selection.start - 1)
                    }
                    selection = TextRange(0)
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    code = ""
                    newSelection = if (selection.end == input.value.text.length) {
                        TextRange(selection.end)
                    } else {
                        TextRange(selection.end + 1)
                    }
                    selection = TextRange(0)
                }
                in arrayOf(KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_DEL) -> {
                    code = ""
                    newSelection = if (selection.start > 0) {
                        if (selection.start == selection.end) {
                            TextRange(selection.start - 1)
                        } else {
                            TextRange(selection.start)
                        }
                    } else {
                        TextRange(0)
                    }
                    if (selection.start == selection.end && selection.start > 0) {
                        selection = TextRange(selection.start - 1, selection.end)
                    }
                }
                else -> {
                    newSelection = TextRange(selection.start + 1)
                }
            }

            val newText = input.value.text.let {
                it.substring(0, selection.start) +
                        code +
                        it.substring(selection.end, it.length)

            }
            input.value = TextFieldValue(newText, newSelection)
            true
        } else false

    }
}