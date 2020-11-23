@file:Suppress("unused")

package tk.mallumo.compose.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import tk.mallumo.compose.navigation.ImplNoteUtils.navNode


@Composable
fun NavigationActivity.NavigationContent() {
    Crossfade(current = navigationViewModel.current) {
        Surface(color = MaterialTheme.colors.background) {
            navNode(node = it.value)
        }
    }
}

object ImplNoteUtils {

    @Suppress("RemoveRedundantQualifierName")
    @Composable
    fun NavigationActivity.navNode(node: ImplNode) {
        when (node.frameID) {

            "tk.sample.app.MenuFrameUI" ->
                composite(navigationViewModel, node) { tk.sample.app.MenuFrameUI() }

            "tk.sample.app.SecondFrameUI" ->
                composite(navigationViewModel, node) { tk.sample.app.SecondFrameUI() }

        }

    }

    @Composable
    private fun composite(
        navigationViewModel: ImplNavigationViewModel,
        node: ImplNode,
        body: @Composable () -> Unit
    ) {
        val argsItem: Any? = remember { buildArgsItem(node) }

        val nav = remember {
            Navigation(navigationViewModel, node.args, node.identifier) {
                argsItem
            }
        }
        Providers(NavigationAmbient provides nav) {
            body()
        }

        onDispose {
            saveArgsItem(node, argsItem)
        }
    }


    private fun buildArgsItem(node: ImplNode): Any? = when (node.frameID) {

        "tk.sample.app.MenuFrameUI" -> tk.sample.app.MenuFrameArgs().fill(node.args)


        "tk.sample.app.SecondFrameUI" -> tk.sample.app.SecondFrameArgs().fill(node.args)


        else -> null
    }

    private fun saveArgsItem(node: ImplNode, argsItem: Any?) {
        when (argsItem) {

            is tk.sample.app.MenuFrameArgs -> node.args = argsItem.asBundle()


            is tk.sample.app.SecondFrameArgs -> node.args = argsItem.asBundle()


        }
    }
}    
