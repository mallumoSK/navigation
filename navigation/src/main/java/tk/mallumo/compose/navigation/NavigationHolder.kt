package tk.mallumo.compose.navigation

import android.os.Bundle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Integer.max

internal class NavigationHolder : ViewModel() {

    private val viewModels = hashSetOf<String>()

    private val nodes = arrayListOf<ImplNode>()

    private val onBackPressHandlers = hashMapOf<String, (() -> Boolean)>()

    private lateinit var viewModelReleaseCallback: (viewModelKey: String) -> Unit

    private val currentInternal by lazy {
        MutableStateFlow(nodes.last())
    }

    val current: StateFlow<ImplNode> get() = currentInternal


    val stackSize: Int get() = nodes.size

    internal fun init(
        startupNode: Node,
        startupArgs: Bundle = Bundle(),
        viewModelReleaseCallback: (viewModelKey: String) -> Unit
    ) {
        this.viewModelReleaseCallback = viewModelReleaseCallback
        if (nodes.isEmpty()) {
            nodes.add(ImplNode(startupNode.id, startupArgs))
        }
    }

    internal fun removeBackStackNodes(indexRange: IntRange) {
        nodes.filterIndexed { index, _ -> index in indexRange }
            .also { nodesToRemove ->
                nodes.removeAll(nodesToRemove)
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
            currentInternal.value = nodes.last()
            true
        }
    }

    internal fun backStackAdd(endOffset: Int, newBackstack: List<Pair<Node, Bundle>>) {
        val newNodes = newBackstack.map { ImplNode(it.first.id, it.second) }
        val backStackIndex = max(0, nodes.size - 1 - endOffset)
        nodes.addAll(backStackIndex, newNodes)
    }

    fun navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean) {
        ImplNode(node.id, args).also {
            if (clearTop) {
                nodes.forEach { stackNode ->
                    releaseNode(stackNode)
                }
                nodes.clear()
            }
            nodes.add(it)
            currentInternal.value = it
        }
    }

    fun nodeViewModelRegister(viewModelKey: String) {
        viewModels.add(viewModelKey)
    }

    fun registerOnBackPressHandler(nodeID: String, onBackPressHandler: () -> Boolean) {
        onBackPressHandlers[nodeID] = onBackPressHandler
    }

    fun handleOnBackPressed(): Boolean = onBackPressHandlers.any {
        try {
            it.value()
        } catch (e: Exception) {
            false
        }
    }

    private fun releaseNode(node: ImplNode) {
        val nodeID = node.identifier
        //release backPress
        onBackPressHandlers.keys
            .filter { it.startsWith(nodeID) }
            .forEach {
                onBackPressHandlers.remove(it)
            }

        //release viewModel
        viewModels.filter { it.startsWith(nodeID) }
            .also { vmToRemove ->
                if (vmToRemove.isNotEmpty()) {
                    viewModels.removeAll(vmToRemove)
                    vmToRemove.forEach {
                        viewModelReleaseCallback(it)
                    }
                }
            }
    }
}