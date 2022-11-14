package tk.mallumo.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import tk.mallumo.compose.navigation.*

@ComposableNavNode
@Composable
fun NewRootUI() {
    ContentWrapper("NewRootUI", Color.Cyan) {
        ScreenAction(
            "Nav to 'AppUI' + clearTop",
            "- remove navigation backstack"
        ) {
            navTo_AppUI(clearTop = true)
        }
        ScreenAction("up -> 1x, App Exit") {
            up(2)
        }
    }
}
