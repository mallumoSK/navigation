package tk.mallumo.compose.navigation

import androidx.compose.runtime.*
import tk.mallumo.compose.navigation.viewmodel.*

internal const val navRootKey = "root"

@Composable
fun Navigation.Companion.rememberNavigationComposite(
    startupNode: Node,
    startupArgs: ArgumentsNavigation?,
    navigationHost: String = navRootKey,
    graph: Graph
): Navigation {
    val isRootNavigation = remember(navigationHost) {
        navigationHost == navRootKey
    }

    return if (isRootNavigation) {
        createRootNavigation(
            startupNode = startupNode,
            startupArgs = startupArgs,
            key = navRootKey,
            graph = graph
        )
    } else {
        createChildNavigation(
            parentNavigation = LocalNavigation.current,
            startupNode = startupNode,
            startupArgs = startupArgs,
            key = navigationHost,
            graph = graph
        )
    }
}


@Composable
internal fun Navigation.Companion.rememberNavigationPreview(
    startupNode: Node,
    startupArgs: ArgumentsNavigation?
): Navigation {
    val vm = globalViewModel(NavigationHolder::class, navRootKey).init(navRootKey, startupNode, startupArgs) {
        //NOTHING
    }


    return remember(navRootKey) {
        object : NavigationWrapper() {

            override val navigationId: String
                get() = navRootKey

            override val graph: Graph
                get() = Graph.Companion.ROOT

            override val viewModelHolder: NavigationHolder
                get() = vm

            override val isPreviewMode: Boolean
                get() = true

        }
    }
}


@Composable
private fun createChildNavigation(
    parentNavigation: Navigation,
    startupNode: Node,
    startupArgs: ArgumentsNavigation?,
    key: String,
    graph:Graph
): Navigation {

    return with(parentNavigation as NavigationWrapper) {

        val releaseTask = SharedPlatform.rememberViewModelRelease()


        val vm = viewModel(NavigationHolder::class, key)

        remember(key) {
            childNavigation
                .firstOrNull { (it as NavigationWrapper).navigationId == key }
                ?: run {
                    vm.init(
                        navigationId = key,
                        startupNode = startupNode,
                        startupArgs = startupArgs,
                        viewModelReleaseCallback = releaseTask
                    )

                    val newChildNavigation = NavigationWrapperInstance(
                        viewModelHolder = vm,
                        navigationId = key,
                        isPreviewMode = parentNavigation.isPreviewMode,
                        graph = graph,
                    ) { stack ->
                        if (vm.stackSize <= stack) {
                            val parentWrapper = (vm.parentNavigation as NavigationWrapper)
                            parentWrapper.up((stack + 1) - vm.stackSize)
                        } else {
                            vm.up(stack)
                        }
                    }

                    vm.parentNavigation = this
                    viewModelHolder.childNavigation += newChildNavigation
                    newChildNavigation
                }
        }
    }
}

@Composable
private fun createRootNavigation(
    startupNode: Node,
    startupArgs: ArgumentsNavigation?,
    @Suppress("SameParameterValue") key: String,
    graph: Graph
): Navigation {

    val releaseTask = SharedPlatform.rememberViewModelRelease()

    val vm = globalViewModel(NavigationHolder::class, key)

    val backPressDispatcher = BackPressDispatcher.rememberBackPressDispatcher(vm)

    DisposableEffect(Unit) {
        onDispose {
            backPressDispatcher.isEnabled = false
        }
    }
    return remember(key) {
        vm.init(
            navigationId = key,
            startupNode = startupNode,
            startupArgs = startupArgs,
            viewModelReleaseCallback = releaseTask
        )
        NavigationWrapperInstance(
            viewModelHolder = vm,
            navigationId = key,
            isPreviewMode = false,
            graph = graph
        ) { stack ->
            if (vm.stackSize < stack + 1) {
                backPressDispatcher.isEnabled = false
                backPressDispatcher.onBackPressed()
                false
            } else {
                backPressDispatcher.isEnabled = true
                vm.up(stack)
            }
        }.apply {
            backPressDispatcher.wrapper = this
        }
    }
}


private class NavigationWrapperInstance(
    override val viewModelHolder: NavigationHolder,
    override val navigationId: String,
    override val isPreviewMode: Boolean,
    override val graph: Graph,
    private val onUp: (stack: Int) -> Boolean,
) : NavigationWrapper() {


    override fun up(stack: Int): Boolean = onUp(stack)
}
