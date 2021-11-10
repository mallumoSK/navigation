package tk.mallumo.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import kotlin.reflect.KClass

/**
 * ViewModel is attached to current navigation node
 * * cleared is after remove from navigation stack
 * * store owner is always hosted activity
 */
@Composable
@Suppress("unused")
inline fun <reified VM : NavigationViewModel> navigationViewModel(
    factory: ViewModelProvider.Factory? = null
): VM = navigationViewModel(VM::class, factory)


/**
 * ViewModel is attached to current navigation node
 * * cleared is after remove from navigation stack
 * * store owner is always hosted activity
 */
@Composable
fun <VM : NavigationViewModel> navigationViewModel(
    modelClass: KClass<VM>,
    factory: ViewModelProvider.Factory? = null
): VM {
    val nav = LocalNavigation.current

    return if (nav.isPreviewMode) {
        remember { modelClass.java.newInstance() }
    } else {
        val viewModelKey = remember(nav.nodeIdentifier) {
            "${nav.nodeIdentifier}${modelClass.qualifiedName}".also {
                nav.registerViewModel(it)
            }
        }
        viewModelByActivity(
            modelClass = modelClass.java,
            key = viewModelKey,
            factory = factory
        )
    }

}

/**
 * ViewModel is attached to current hosted activity
 * * cleared is while destroy of activity
 * * store owner is always hosted activity
 */
@Composable
fun <VM : ViewModel> viewModelByActivity(
    modelClass: Class<VM>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): VM {
    val ctx = LocalContext.current
    val view =  LocalView.current
   return remember("${modelClass.simpleName}-$key") {
        if(ctx is ViewModelStoreOwner){
            ctx.get(modelClass, key, factory)
        }else{
            view.findViewTreeViewModelStoreOwner()?.get(modelClass, key, factory)
                ?: throw Exception("View model store owner not found")
        }
    }
}

/**
 * ViewModel is attached to store owner of composable view root
 * * cleared is while destroy of activity or fragment
 * * store owner is activity or fragment
 */
@Composable
@Suppress("unused")
fun <VM : ViewModel> viewModelByCurrent(
    modelClass: Class<VM>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null
): VM = LocalView.current.let {
    remember("${modelClass.simpleName}-$key") {
        it.findViewTreeViewModelStoreOwner()?.get(modelClass, key, factory)
            ?: throw Exception("View model store owner not found")
    }
}

private fun <VM : ViewModel> ViewModelStoreOwner.get(
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

abstract class NavigationViewModel : ViewModel() {
    abstract override fun onCleared()
}