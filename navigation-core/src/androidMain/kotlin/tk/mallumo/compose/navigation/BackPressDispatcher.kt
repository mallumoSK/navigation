package tk.mallumo.compose.navigation

import androidx.activity.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*

internal actual class BackPressDispatcher(
    activity: ComponentActivity,
    private val holder: NavigationHolder,
    private val appQuitEnabled: Boolean
) {

    actual var wrapper: NavigationWrapper? = null


    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!holder.handleOnBackPressed()) {
                wrapper?.up(1)
            }
        }
    }

    private val dispatcher = activity.onBackPressedDispatcher.apply {
        addCallback(backPressCallback)
    }


    actual var isEnabled: Boolean
        get() = backPressCallback.isEnabled
        set(value) {
            backPressCallback.isEnabled = value
        }

    actual fun onBackPressed() {
        if(appQuitEnabled) dispatcher.onBackPressed()
    }

    actual companion object {
        @Composable
        actual fun rememberBackPressDispatcher(holder: NavigationHolder, appQuitEnabled: Boolean): BackPressDispatcher {
            val ctx = LocalContext.current

            return remember {
                BackPressDispatcher(ctx as ComponentActivity, holder, appQuitEnabled)
            }
        }

    }


}
