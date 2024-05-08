package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*

internal abstract class NavigationWrapper : Navigation {

    override val testing: ImplNavTesting
        get() = object : ImplNavTesting {
            override val navigation: Navigation
                get() = this@NavigationWrapper
        }

    abstract val viewModelHolder: NavigationHolder

    override val nodeIdentifier: String get() = currentNode.value.identifier

    @Suppress("unused")
    override val currentNode: StateFlow<NodeInfo>
        get() = viewModelHolder.current

    override val childNavigation: List<Navigation>
        get() = viewModelHolder.childNavigation

    override val parentNavigation: Navigation?
        get() = viewModelHolder.parentNavigation

    override fun up(stack: Int) = viewModelHolder.up(stack)

    override fun navigateTo(node: Node, args: ArgumentsNavigation, clearTop: Boolean) {
        viewModelHolder.navigateTo(node, args, clearTop)
    }

    override fun canGoUp(): Boolean = backStack.size() > 0 || (parentNavigation?.canGoUp()?:false)

    @Composable
    override fun onBackPress(consume: () -> Boolean) {
        DisposableEffect(Unit) {
            val bpKey = viewModelHolder.registerOnBackPressHandler(consume)
            onDispose {
                viewModelHolder.unregisterOnBackPressHandler(bpKey)
            }
        }

    }

    //TODO separe for root + childs
    override val backStack by lazy {

        object : ImplBackStack {

            override fun add(endOffset: Int, nodes: List<Pair<Node, ArgumentsNavigation>>) =
                viewModelHolder.backStackAdd(endOffset, nodes)

            override fun clearAll() = clear()

            override fun clear(startOffset: Int, endOffset: Int) = viewModelHolder.backStackClear(0, 0)

            override fun size(): Int = viewModelHolder.stackSize -1
        }
    }
}
