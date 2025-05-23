package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*

interface Navigation {

    val navigationId: String

    val graph: Graph

    val isPreviewMode: Boolean

    val nodeIdentifier: String

    val childNavigation: List<Navigation>

    val parentNavigation: Navigation?

    val currentNode: StateFlow<NodeInfo>

    val backStack: ImplBackStack

    val testing: ImplNavTesting

    fun findChildNavigation(graph: Graph): Navigation? {
        childNavigation.forEach {
            if (it.graph == graph) return it
            it.findChildNavigation(graph)?.also {
                return it
            }
        }
        return null
    }

    companion object {
        @Suppress("unused", "FunctionName")
        @Composable
        fun Preview(
            startupNode: Node = Node(navRootKey),
            startupArgs: ArgumentsNavigation? = null,
            content: @Composable () -> Unit
        ) {
            val nav = rememberNavigationPreview(
                startupNode = startupNode,
                startupArgs = startupArgs
            )
            CompositionLocalProvider(LocalNavigation provides nav) {
                content()
            }
        }
    }

    fun up(stack: Int = 1): Boolean

    fun canGoUp(): Boolean

    fun navigateTo(node: Node, args: ArgumentsNavigation = ArgumentsNavigation(), clearTop: Boolean)

    @ExtNavMarker
    @Composable
    fun onBackPress(consume: () -> Boolean)
}
