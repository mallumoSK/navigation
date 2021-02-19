package tk.mallumo.compose.navigation

import android.os.Bundle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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


    fun up(stack: Int = 1) {
        repeat(stack) {
            val removedNode = nodes.removeLast()
            releaseNode(removedNode)
        }
        currentInternal.value = nodes.last()
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