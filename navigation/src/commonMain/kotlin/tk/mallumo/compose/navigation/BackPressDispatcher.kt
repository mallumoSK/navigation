package tk.mallumo.compose.navigation

import androidx.compose.runtime.*

internal expect class BackPressDispatcher {
    var isEnabled: Boolean

    var wrapper: NavigationWrapper?

    fun onBackPressed()

    companion object {
        @Composable
        fun rememberBackPressDispatcher(holder: NavigationHolder): BackPressDispatcher

    }

}
