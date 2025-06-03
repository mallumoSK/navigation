package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.viewmodel.*
import kotlin.printStackTrace

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class SharedPlatform {
    actual companion object {
        @Composable
        actual fun rememberViewModelRelease(): (key: String) -> Unit {
            return remember {
                { key ->
                    runCatching {
                        viewModels.remove(key)?.releaseInternal()
                    }.onFailure {
                        println("viewmodel release error: [$key]")
                        it.printStackTrace()
                    }
                }
            }
        }
    }
}
