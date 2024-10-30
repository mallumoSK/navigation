package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.viewmodel.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class SharedPlatform {
    actual companion object {
        @Composable
        actual fun rememberViewModelRelease(): (key: String) -> Unit {
            return remember {
                { key ->
                    viewModels.remove(key)?.releaseInternal()
                }
            }
        }
    }
}