package tk.mallumo.compose.navigation

@Suppress("unused")
interface ImplNavTesting {

    val navigation: Navigation

    val currentNodeId: String get() = navigation.nodeIdentifier

    val currentNodeVmIds: Set<String>
        get() = with(navigation as NavigationWrapper) {
            viewModelHolder.nodes.last().second
        }

    val currentNavigationNodeIds: List<String>
        get() = with(navigation as NavigationWrapper) {
            viewModelHolder.nodes.map { it.first.identifier }
        }

    val isRootNode
        get() = navigation.parentNavigation == null

    val childNavigationIds
        get() = navigation.childNavigation.map { it.navigationId }

    val currentNodeBackPressCallbackIds
        get() = with(navigation as NavigationWrapper) {
            val prefix = "$navigationId[$currentNodeId]"
            viewModelHolder.findRootNavigationHolder()
                .onBackPressHandlers
                .filter { it.first.startsWith(prefix) }
                .map { it.first }
        }

    val currentNavigationBackStackIds: List<String>
        get() = currentNavigationNodeIds.dropLast(1)
}
