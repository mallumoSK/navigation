package tk.mallumo.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import tk.mallumo.compose.navigation.*

@ComposableNavNode
@Composable
fun NestedChild0_FirstUI() {
    ContentWrapper("NestedChild0MainUI", Color.Yellow) {
        ScreenAction(
            "Nav to 'NestedChild0SecondFrameUI'",
            "- this navigation is executed inside 'child0' navigation"
        ) {
            navTo_NestedChild0_SecondUI()
        }
    }
}

@ComposableNavNode
@Composable
fun NestedChild0_SecondUI() {
    ContentWrapper("NestedChild0SecondFrameUI", Color.Red) {
        ScreenAction(
            "up -> 1x",
            "- back into 'NestedChild0MainUI'"
        ) {
            up()
        }
        ScreenAction(
            "up -> 2x",
            "- back into 'AppUI'\n-remove parent 'NestedChild0MainUI'\n- remove 'Screen1UI' from root navigation"
        ) {
            up(2)
        }
        ScreenAction(
            "up -> 3x",
            "- App exit by remove all parrents"
        ) {
            up(3)
        }
    }
}
