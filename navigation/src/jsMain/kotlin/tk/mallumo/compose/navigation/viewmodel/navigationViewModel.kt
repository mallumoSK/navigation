package tk.mallumo.compose.navigation.viewmodel

import androidx.compose.runtime.*
import kotlin.reflect.*


internal val viewModels = mutableMapOf<String, SharedViewModel>()

@Composable
actual fun <VM : SharedViewModel> globalViewModel(
    modelClass: KClass<VM>,
    key: String?
): VM {
    val id = key ?: modelClass.simpleName!!
    return remember(id) {
        getViewModel(modelClass, id)
    }
}

internal fun <VM : SharedViewModel> getViewModel(modelClass: KClass<VM>, key: String): VM {
    @Suppress("UNCHECKED_CAST")
    return viewModels.getOrPut(key) {
        ViewModelFactory.instanceOf(modelClass)
    } as VM
}
