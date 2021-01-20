@file:Suppress("unused")

package tk.mallumo.compose.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

val AmbientNavigation = ambientOf<Navigation> { error("Unexpected error") }

@Deprecated(
    message = "Old type of declaration",
    replaceWith = ReplaceWith(
        "AmbientNavigation",
        "tk.mallumo.compose.navigation.AmbientNavigation"
    )
)
val NavigationAmbient
    get() = AmbientNavigation


data class Node(val id: String) {
    companion object
}

@Composable
fun <VM : ViewModel> navigationViewModel(
    modelClass: KClass<VM>,
    factory: ViewModelProvider.Factory? = null
): VM {

    val nodeID = AmbientNavigation.current.nodeIdentifier
    val viewModelKey = "$nodeID${modelClass.qualifiedName}"

    viewModel(ImplNavigationViewModel::class.java)
        .nodeViewModelRegister(viewModelKey)

    return viewModel(
        modelClass = modelClass.java,
        key = viewModelKey,
        factory = factory
    )
}


class Navigation constructor(
    private val navigationViewModel: ImplNavigationViewModel,
    val args: Bundle,
    val nodeIdentifier: String,
    private val bundledCallback: () -> Any?
) {

    /**
     * if this function return true, it means back press is consumed inside compose layout
     * Default = false
     */
    var consumeBackNavigation = { false }

    companion object {
        val preview
            get() = Navigation(ImplNavigationViewModel(), Bundle(), "-") {
                null
            }

        @SuppressLint("ComposableNaming")
        @Composable
        fun preview(args: Any, argsBundle: Bundle = Bundle()): Navigation = remember(args) {
            Navigation(ImplNavigationViewModel(), argsBundle, "-") {
                args
            }
        }
    }

    init {
        navigationViewModel.registerActiveNavigation(nodeIdentifier) {
            consumeBackNavigation()
        }
    }


    fun up(stack: Int = 1): Boolean = navigationViewModel.up(stack)

    fun navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean) {
        navigationViewModel.navigateTo(node, args, clearTop)
    }


    inline fun <reified T : Any> bundledArgs() = bundledArgs(T::class)

    fun <T : Any> bundledArgs(type: KClass<T>): T {
        val item = bundledCallback()
        @Suppress("UNCHECKED_CAST")
        return when {
            item == null -> throw Exception("Navigation node has no arguments")
            item::class != type -> throw Exception("Invalid type of args bound to Navigation node, must be ${item::class.qualifiedName}")
            else -> item as T
        }
    }
}