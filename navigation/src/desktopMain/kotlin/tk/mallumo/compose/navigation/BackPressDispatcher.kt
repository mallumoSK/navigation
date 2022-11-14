package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import kotlin.system.*

internal actual class BackPressDispatcher {

    actual var isEnabled: Boolean
        get() = true
        set(_) {}

    actual fun onBackPressed() {
        exitProcess(0)
    }

    actual companion object {
        @Composable
        actual fun rememberBackPressDispatcher(holder: NavigationHolder): BackPressDispatcher {
            return remember {
                BackPressDispatcher()
            }
        }

    }

    actual var wrapper: NavigationWrapper?
        get() = null
        set(_) {}


}
