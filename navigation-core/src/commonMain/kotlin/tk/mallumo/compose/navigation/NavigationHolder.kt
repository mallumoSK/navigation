package tk.mallumo.compose.navigation


import kotlinx.coroutines.flow.*
import tk.mallumo.compose.navigation.viewmodel.*
import kotlin.math.*

internal class NavigationHolder : NavigationViewModel() {

    val childNavigation: MutableList<Navigation> = mutableListOf()

    var parentNavigation: Navigation? = null

    internal val nodes = arrayListOf<Pair<NodeInfo, MutableSet<String>>>()

    internal val onBackPressHandlers = mutableListOf<Pair<String, (() -> Boolean)>>()

    private lateinit var viewModelReleaseCallback: (viewModelKey: String) -> Unit

    private val currentInternal by lazy {
        MutableStateFlow(nodes.last().first)
    }

    val current: StateFlow<NodeInfo> get() = currentInternal


    val stackSize: Int get() = nodes.size

    private var isInitialized = false

    private lateinit var navigationId: String

    internal fun init(
        navigationId: String,
        startupNode: Node,
        startupArgs: ArgumentsNavigation?,
        viewModelReleaseCallback: (viewModelKey: String) -> Unit
    ): NavigationHolder {
        this.viewModelReleaseCallback = viewModelReleaseCallback

        if (isInitialized) return this
        this.navigationId = navigationId
        if (nodes.isEmpty()) {
            nodes.add(NodeInfo(startupNode.id, startupArgs ?: ArgumentsNavigation()) to mutableSetOf())
        }
        return this
    }

    @Suppress("unused")
    fun backStackClearAll() = backStackClear(0, 0)

    fun backStackClear(startOffset: Int, endOffset: Int) {
        val range = (startOffset) until (stackSize - endOffset - 1)
        removeBackStackNodes(range)
    }

    private fun removeBackStackNodes(indexRange: IntRange) {
        nodes.filterIndexed { index, _ -> index in indexRange }
            .also { nodesToRemove ->
                nodes.removeAll(nodesToRemove.toSet())
                nodesToRemove.forEach { releaseNode(it) }
            }
    }

    fun up(stack: Int = 1): Boolean {
        return if (nodes.size <= 1) false
        else {
            repeat(stack) {
                val removedNode = nodes.removeLast()
                releaseNode(removedNode)
            }
            try {
                currentInternal.value = nodes.last().first
            } catch (e: Throwable) {
                throw Exception(navigationId, e)
            }
            true
        }
    }

    internal fun backStackAdd(endOffset: Int, newBackstack: List<Pair<Node, ArgumentsNavigation>>) {
        val newNodes = newBackstack.map { NodeInfo(it.first.id, it.second) to mutableSetOf<String>() }
        val backStackIndex = max(0, nodes.size - 1 - endOffset)
        nodes.addAll(backStackIndex, newNodes)
    }

    fun navigateTo(node: Node, args: ArgumentsNavigation = ArgumentsNavigation(), clearTop: Boolean) {
        NodeInfo(node.id, args).also {
            if (clearTop) {
                nodes.forEach { stackNode ->
                    releaseNode(stackNode)
                }
                nodes.clear()
            }
            nodes.add(it to mutableSetOf())
            currentInternal.value = it
        }
    }

    internal fun findRootNavigationHolder(): NavigationHolder {
        val parentWrapper = parentNavigation as? NavigationWrapper
        return parentWrapper
            ?.viewModelHolder
            ?.findRootNavigationHolder()
            ?: this
    }

    private var onBackPressHandlerIdGen = 0


    fun registerOnBackPressHandler(onBackPressHandler: () -> Boolean): String {
        return buildBackPressKey(true).also {
            findRootNavigationHolder()
                .onBackPressHandlers += (it to onBackPressHandler)
        }

    }

    private fun buildBackPressKey(genId: Boolean, nodeId: String = current.value.identifier) = buildString {
        append("${navigationId}[$nodeId].onBackPress")
        if (genId) append("(${onBackPressHandlerIdGen++})")
    }

    fun unregisterOnBackPressHandler(bpKey: String) {
        findRootNavigationHolder()
            .onBackPressHandlers
            .run {
                firstOrNull { it.first == bpKey }?.also {
                    remove(it)
                }
            }
    }

    fun handleOnBackPressed(): Boolean {
        val prefix = buildBackPressKey(false)
        return findRootNavigationHolder()
            .onBackPressHandlers
            .filter { it.first.startsWith(prefix) }
            .any {
                try {
                    it.second()
                } catch (e: Exception) {
                    false
                }
            }
    }

    private fun releaseNode(source: Pair<NodeInfo, Set<String>>) {
        val (node, vmIds) = source

        //release backPress
        unregisterOnBackPressHandler(buildBackPressKey(false, node.identifier))

        val childNavigationPairs = childNavigation
            .map { it.parentNavigation?.buildViewModelKeyFull(it.navigationId, NavigationHolder::class) to it }

        //release viewModel
        vmIds.forEach { key ->
            viewModelReleaseCallback(key)
            childNavigationPairs.firstOrNull { it.first == key }
                ?.second
                ?.also {
                    childNavigation.remove(it)
                }
        }
    }

    override fun onRelease() {
        nodes.forEach(::releaseNode)
    }

    fun registerViewModel(viewModelKey: String) {
        nodes.last().second += viewModelKey
    }


}
