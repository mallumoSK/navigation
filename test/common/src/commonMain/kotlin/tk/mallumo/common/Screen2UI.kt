package tk.mallumo.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import tk.mallumo.compose.navigation.*

@ComposableNavNode
@Composable
fun Screen2UI() {
    ContentWrapper("Screen2UI", Color.Green) {
        ScreenAction("up -> 1x", "- Back to screen 'Screen1UI'") {
            up()
        }
        ScreenAction("up -> 2x", "- Back to screen 'AppUI'") {
            up(2)
        }
        ScreenAction("up -> 3x", "- App exit") {
            up(3)
        }
    }
}
