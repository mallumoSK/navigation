package tk.mallumo.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass


@Composable
@Suppress("unused")
inline fun <reified VM : NavigationViewModel> navigationViewModel(
    factory: ViewModelProvider.Factory? = null
): VM = navigationViewModel(VM::class, factory)

@Composable
fun <VM : NavigationViewModel> navigationViewModel(
    modelClass: KClass<VM>,
    factory: ViewModelProvider.Factory? = null
): VM {
    val nav = AmbientNavigation.current

    return if (nav.isPreviewMode) {
        modelClass.java.newInstance()
    } else {
        val viewModelKey = remember(nav.nodeIdentifier) {
            "${nav.nodeIdentifier}${modelClass.qualifiedName}".also {
                nav.registerViewModel(it)
            }
        }
        viewModel(
            modelClass = modelClass.java,
            key = viewModelKey,
            factory = factory
        )
    }

}

abstract class NavigationViewModel : ViewModel() {
    abstract override fun onCleared()
}