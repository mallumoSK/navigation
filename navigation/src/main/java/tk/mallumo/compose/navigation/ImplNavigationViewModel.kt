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

    val current by lazy {
        mutableStateOf(nodes.last())
    }

    fun up(stack: Int = 1): Boolean {
        if (nodes.size <= stack) return false
        repeat(stack) {
            nodeViewModelRelease(nodes.removeLast())
        }
        current.value = nodes.last()
        return true
    }

    fun navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean) {
        ImplNode(node.id, args).also {
            if (clearTop) {
                nodes.forEach { stackNode -> nodeViewModelRelease(stackNode) }
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
}