package tk.mallumo.compose.navigation

import androidx.compose.runtime.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class SharedPlatform {
    companion object {
        @Composable
        fun rememberViewModelRelease(): (key: String) -> Unit
    }
}
