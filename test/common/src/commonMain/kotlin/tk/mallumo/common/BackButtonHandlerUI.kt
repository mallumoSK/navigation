package tk.mallumo.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import tk.mallumo.compose.navigation.*

@ComposableNavNode
@Composable
fun BackButtonHandlerUI() {
    val nav = LocalNavigation.current
    var backPressCounter by remember {
        mutableStateOf(2)
    }
    if (backPressCounter > 0) {
        nav.onBackPress {
            (backPressCounter > 0).also {
                if (it) backPressCounter -= 1
            }
        }
    }
    ContentWrapper("BackButtonHandlerUI", Color.LightGray) {

        Text(
            text = "Back to handle: $backPressCounter",
            style = MaterialTheme.typography.h6
        )

        Spacer(Modifier.size(16.dp))
        ScreenAction("up -> 1x, back into AppUI") {
            up()
        }
    }
}
