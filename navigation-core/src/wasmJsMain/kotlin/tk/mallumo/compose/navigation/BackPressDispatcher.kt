package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import kotlinx.browser.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class BackPressDispatcher {

    actual var isEnabled: Boolean
        get() = true
        set(_) {}

    actual fun onBackPressed() {
        window.history.back()
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
