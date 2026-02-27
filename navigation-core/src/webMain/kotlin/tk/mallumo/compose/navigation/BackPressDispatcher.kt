package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import kotlinx.browser.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class BackPressDispatcher(private val appQuitEnabled: Boolean) {

    actual var isEnabled: Boolean
        get() = true
        set(_) {}

    actual fun onBackPressed() {
        if(appQuitEnabled)   window.history.back()
    }

    actual companion object {
        @Composable
        actual fun rememberBackPressDispatcher(holder: NavigationHolder, appQuitEnabled: Boolean): BackPressDispatcher {
            return remember {
                BackPressDispatcher(appQuitEnabled)
            }
        }

    }

    actual var wrapper: NavigationWrapper?
        get() = null
        set(_) {}


}
