package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.viewmodel.*

internal actual class SharedPlatform {

    actual companion object {
        @Composable
        actual fun rememberViewModelRelease(): (key: String) -> Unit {
            return remember {
                val releaseTask: (key: String) -> Unit = { viewModelKey ->
                    Store.release(viewModelKey)
                }
                releaseTask
            }
        }
    }
}
