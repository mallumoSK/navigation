package tk.mallumo.compose.navigation.viewmodel

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import tk.mallumo.compose.navigation.*
import kotlin.reflect.*

class EmptySharedViewModel : SharedViewModel() {
    override fun onRelease() = Unit
}

@Suppress("unused")
class EmptyNavigationViewModel : NavigationViewModel() {
    override fun onRelease() = Unit
}

@Composable
fun <VM : NavigationViewModel> Navigation.viewModel(modelClass: KClass<VM>, key: String? = null): VM =
    viewModelInternal(modelClass, key)

@Composable
expect fun <VM : SharedViewModel> globalViewModel(modelClass: KClass<VM>, key: String? = null): VM

@Composable
internal fun <VM : NavigationViewModel> Navigation.viewModelInternal(modelClass: KClass<VM>, key: String?): VM {

    return if (isPreviewMode) {
        remember { ViewModelFactory.instanceOf(modelClass) }
    } else {
        val viewModelKey = remember(nodeIdentifier) {
            buildViewModelKeyFull(key, modelClass).also {
                (this as NavigationWrapper).viewModelHolder.registerViewModel(it)
            }
        }
        globalViewModel(
            modelClass = modelClass,
            key = viewModelKey
        )
    }
}

internal fun Navigation.buildViewModelKeyFull(key: String?, clazz: KClass<*>): String {
    return "${navigationId}.node[${nodeIdentifier}]_${key ?: ""}(${clazz.qName})"
}

internal fun SharedViewModel.releaseScope() {
    internalScopeRef.runCatching {
        cancel()
    }.onFailure {
        println("release of VM scope error")
        it.printStackTrace()
    }
    ViewModelFactory.release(this)
}

private var scopeNameAtomic = 0


internal fun createViewModelScope(clazz: KClass<out SharedViewModel>): CoroutineScope {
    return CoroutineScope(CoroutineName(clazz::class.qName))
}
