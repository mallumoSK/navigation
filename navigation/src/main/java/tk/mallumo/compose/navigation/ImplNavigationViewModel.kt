@file:Suppress("MemberVisibilityCanBePrivate")

package tk.mallumo.compose.navigation

import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


@Suppress("unused")
class ImplNavigationViewModel : ViewModel() {

    private val viewModels = hashSetOf<String>()
    private val nodes = arrayListOf<ImplNode>()
    private val implViewModelsToRelease = MutableSharedFlow<String>()
    internal val viewModelsToRelease = implViewModelsToRelease.asSharedFlow()

    private val backNavigationConsumers = hashMapOf<String, (() -> Boolean)>()

    val current by lazy {
        mutableStateOf(nodes.last())
    }

    fun back(): Boolean =
        if (backNavigationConsumers.values.any { consumer -> consumer() }) {
            true
        } else {
            up(1)
        }

    fun up(stack: Int = 1): Boolean {
        if (nodes.size <= stack) return false
        repeat(stack) {
            val removedNode = nodes.removeLast()
            nodeViewModelRelease(removedNode)
            unregisterActiveNavigation(removedNode.identifier)
        }
        current.value = nodes.last()
        return true
    }

    fun navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean) {
        ImplNode(node.id, args).also {
            if (clearTop) {
                nodes.forEach { stackNode ->
                    unregisterActiveNavigation(stackNode.identifier)
                    nodeViewModelRelease(stackNode)
                }
                nodes.clear()
            }
            nodes.add(it)
            current.value = it
        }
    }

    internal fun init(startupNode: Node, startupArgs: Bundle = Bundle()) {
        nodes.add(ImplNode(startupNode.id, startupArgs))
    }

    fun nodeViewModelRegister(viewModelKey: String) {
        viewModels.add(viewModelKey)
    }

    private fun nodeViewModelRelease(node: ImplNode) {
        viewModels.filter { it.startsWith(node.identifier) }
            .also { vmToRemove ->
                if (vmToRemove.isNotEmpty()) {
                    viewModels.removeAll(vmToRemove)
                    GlobalScope.launch {
                        vmToRemove.forEach {
                            implViewModelsToRelease.emit(it)
                        }
                    }
                }
            }
    }

    fun registerActiveNavigation(nodeIdentifier: String, consumer: () -> Boolean) {
        backNavigationConsumers[nodeIdentifier] = consumer
    }

    private fun unregisterActiveNavigation(nodeIdentifier: String) {
        backNavigationConsumers.remove(nodeIdentifier)
    }
}