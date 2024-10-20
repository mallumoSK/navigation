package tk.mallumo.compose.navigation

import androidx.compose.runtime.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class BackPressDispatcher {
    var isEnabled: Boolean

    var wrapper: NavigationWrapper?

    fun onBackPressed()

    companion object {
        @Composable
        fun rememberBackPressDispatcher(holder: NavigationHolder): BackPressDispatcher

    }

}
