package tk.mallumo.compose.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.StateFlow

interface NavigationComposite {
    val currentNode: StateFlow<ImplNode>

    fun up(stack: Int = 1)
    fun backStackAdd(endOffset: Int = 0, nodes: List<Pair<Node, Bundle>>)
    fun backStackClearAll()
    fun backStackClear(startOffset: Int = 0, endOffset: Int = 0)
    fun navigateTo(node: Node, args: Bundle = Bundle(), clearTop: Boolean = false)
    fun nodeViewModelRegister(viewModelKey: String)
    fun registerOnBackPressHandler(nodeID: String, onBackPressHandler: () -> Boolean)

    companion object {
        fun get(
            composite: ComponentActivity,
            startupNode: Node,
            startupArgs: Bundle?
        ): NavigationComposite {
            return NavigationCompositeImpl(
                composite = composite,
                startupNode = startupNode,
                startupArgs = startupArgs ?: composite.intent.extras ?: Bundle()
            )
        }
    }
}

private class NavigationCompositeImpl(
    composite: ComponentActivity,
    startupNode: Node,
    startupArgs: Bundle
) : NavigationComposite {

    private val viewModelProvider by lazy { ViewModelProvider(composite) }

    private val viewModelReleaseCallback: (viewModelKey: String) -> Unit = { viewModelKey ->
        viewModelProvider.get(viewModelKey, EmptyViewModel::class.java)
    }

    private val viewModel by lazy {
        viewModelProvider[NavigationHolder::class.java].apply {
            init(
                startupNode = startupNode,
                startupArgs = startupArgs,
                viewModelReleaseCallback = viewModelReleaseCallback
            )
        }
    }
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!viewModel.handleOnBackPressed()) {
                up(1)
            }
        }
    }

    private val backPressDispatcher = composite.onBackPressedDispatcher.apply {
        addCallback(backPressCallback)
    }


    override val currentNode: StateFlow<ImplNode>
        get() = viewModel.current

    override fun up(stack: Int) {
        if (viewModel.stackSize <= stack) {
            backPressCallback.isEnabled = false
            backPressDispatcher.onBackPressed()
        } else {
            backPressCallback.isEnabled = true
            viewModel.up(stack)
        }
    }

    override fun backStackAdd(endOffset: Int, nodes: List<Pair<Node, Bundle>>) {
        viewModel.backStackAdd(endOffset,nodes )
    }

    override fun backStackClearAll() = backStackClear(0, 0)

    override fun backStackClear(startOffset: Int, endOffset: Int) {
        val range = (startOffset) until (viewModel.stackSize - endOffset -1)
        viewModel.removeBackStackNodes(range)
    }

    override fun navigateTo(node: Node, args: Bundle, clearTop: Boolean) {
        viewModel.navigateTo(node = node, args = args, clearTop = clearTop)
    }

    override fun nodeViewModelRegister(viewModelKey: String) {
        viewModel.nodeViewModelRegister(viewModelKey)
    }

    override fun registerOnBackPressHandler(nodeID: String, onBackPressHandler: () -> Boolean) {
        viewModel.registerOnBackPressHandler(nodeID, onBackPressHandler)
    }
}