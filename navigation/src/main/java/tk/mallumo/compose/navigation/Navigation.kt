package tk.mallumo.compose.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass


val LocalNavigation = staticCompositionLocalOf<Navigation> { error("Unexpected error") }

@Deprecated("Use LocalNavigation", replaceWith = ReplaceWith("LocalNavigation"))
val AmbientNavigation  get() = LocalNavigation

@Suppress("unused")
fun ComponentActivity.navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean = false) {
    ViewModelProvider(this)[NavigationHolder::class.java]
        .navigateTo(node, args, clearTop)
}

@Suppress("unused")
fun ComponentActivity.up(stack: Int = 1) {
    ViewModelProvider(this)[NavigationHolder::class.java]
        .up(stack)
}

data class Node(val id: String) {
    companion object
}


class Navigation constructor(
    private val navigationComposite: NavigationComposite,
    val args: Bundle,
    val nodeIdentifier: String,
    internal val isPreviewMode: Boolean = false,
    private val bundledCallback: () -> Any?
) {

    /**
     * if this function return true, it means back press is consumed inside compose layout
     * Default = false
     */
    companion object {
        private val previewNavigationComposite by lazy {
            object : NavigationComposite {
                override val currentNode: StateFlow<ImplNode>
                    get() = MutableStateFlow(ImplNode("default-sample", Bundle()))

                override fun up(stack: Int) {}
                override fun navigateTo(node: Node, args: Bundle, clearTop: Boolean) {}
                override fun nodeViewModelRegister(viewModelKey: String) {}
                override fun registerOnBackPressHandler(
                    nodeID: String,
                    onBackPressHandler: () -> Boolean
                ) {
                }
            }
        }

        @Suppress("unused")
        @SuppressLint("ComposableNaming")
        @Composable
        fun preview(
            args: Any? = null,
            argsBundle: Bundle = Bundle()
        ): Navigation = remember(args) {
            Navigation(previewNavigationComposite, argsBundle, "-", true) {
                args
            }
        }
    }

    @Suppress("unused")
    fun up(stack: Int = 1) = navigationComposite.up(stack = stack)

    fun navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean) {
        navigationComposite.navigateTo(node, args, clearTop)
    }

    @Suppress("unused")
    fun onBackPress(key: String = "", body: () -> Boolean) {
        navigationComposite.registerOnBackPressHandler("$nodeIdentifier:$key", body)
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

    internal fun registerViewModel(viewModelKey: String) {
        navigationComposite.nodeViewModelRegister(viewModelKey)
    }
}