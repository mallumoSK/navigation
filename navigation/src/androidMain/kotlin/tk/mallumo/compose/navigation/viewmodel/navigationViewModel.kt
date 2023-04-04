package tk.mallumo.compose.navigation.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import tk.mallumo.compose.navigation.*
import kotlin.reflect.*

@Composable
actual fun <VM : SharedViewModel> globalViewModel(
    modelClass: KClass<VM>,
    key: String?
): VM {
    val ctx = LocalContext.current
    val view = LocalView.current

    val id = key ?: modelClass.qName

    return remember(id) {
        if (ctx is ViewModelStoreOwner) {
            ctx.get(modelClass.java, id, null)
        } else {
            view.findViewTreeViewModelStoreOwner()?.get(modelClass.java, id, null)
                ?: throw Exception("View model store owner not found")
        }
    }
}

inline fun <reified VM : SharedViewModel> ViewModelStoreOwner.vm(key: String? = null): VM = vm(VM::class, key)

fun <VM : SharedViewModel> ViewModelStoreOwner.vm(modelClass: KClass<VM>, key: String?): VM {
    val id = key ?: modelClass.qName
    return get(modelClass.java, id, null)
}

internal fun <VM : ViewModel> ViewModelStoreOwner.get(
    javaClass: Class<VM>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): VM {
    val provider = if (factory != null) {
        ViewModelProvider(this, factory)
    } else {
        ViewModelProvider(this)
    }
    return if (key != null) {
        provider[key, javaClass]
    } else {
        provider[javaClass]
    }
}
