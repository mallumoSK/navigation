package tk.mallumo.compose.navigation.viewmodel

import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.*
import kotlin.reflect.*

@Composable
actual fun <VM : SharedViewModel> globalViewModel(
    modelClass: KClass<VM>,
    key: String?
): VM {
    val id = key ?: modelClass.qName
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
