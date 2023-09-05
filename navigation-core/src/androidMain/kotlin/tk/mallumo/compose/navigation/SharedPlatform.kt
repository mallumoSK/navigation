package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import tk.mallumo.compose.navigation.viewmodel.*

internal actual class SharedPlatform {

    actual companion object {
        @Composable
        actual fun rememberViewModelRelease(): (key: String) -> Unit {
            val ctx = LocalContext.current
            val view = LocalView.current
            return remember {

                val provider = if (ctx is ViewModelStoreOwner) ViewModelProvider(ctx)
                else ViewModelProvider(view.findViewTreeViewModelStoreOwner()!!)

                val releaseTask: (key: String) -> Unit = { viewModelKey ->
                    provider[viewModelKey, EmptySharedViewModel::class.java]
                }

                releaseTask
            }
        }
    }
}
