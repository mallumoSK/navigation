package tk.mallumo.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import tk.mallumo.compose.navigation.*

@ComposableNavNode
@Composable
fun AppUI() {
    ContentWrapper("AppUI", Color.LightGray) {
        ScreenAction("Nav to 'Screen1UI'") {
            navTo_Screen1UI()
        }

        ScreenAction("Nav to 'BackButtonHandlerUI'") {
            navTo_BackButtonHandlerUI()
        }

        ScreenAction(
            "Nav to 'Screen1UI' + child nav.",
            "- navigate into Screen1UI with activated child navigation"
        ) {
            navTo_Screen1UI(ArgsScreen1UI(showChildNavigation = true))
        }

        ScreenAction("Exit APP") {
            up()
        }
    }
}
