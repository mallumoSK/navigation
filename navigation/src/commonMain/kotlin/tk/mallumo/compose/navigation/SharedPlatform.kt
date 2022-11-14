package tk.mallumo.compose.navigation

import androidx.compose.runtime.*

internal expect class SharedPlatform {
    companion object {
        @Composable
        fun rememberViewModelRelease(): (key: String) -> Unit
    }
}
