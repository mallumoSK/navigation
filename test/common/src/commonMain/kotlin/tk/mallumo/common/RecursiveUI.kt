package tk.mallumo.common

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import tk.mallumo.compose.navigation.*
import tk.mallumo.compose.navigation.viewmodel.*

data class ArgsRecursiveUI(var recursion: Int = 0)


@VM
class RecursiveVM : NavigationViewModel() {
    override fun onRelease() {
        println("RecursiveVM onRelease")
    }

    fun isScopeActive(): Boolean = scope.isActive

}

@ComposableNavNode(ArgsRecursiveUI::class)
@Composable
fun RecursiveUI() {
    val nav = LocalNavigation.current
    val navArgs = LocalNavigationArgs.current
    val args = navArgs.rememberArgs(ArgsRecursiveUI::class)
    val vm = nav.viewModel(RecursiveVM::class)

    ContentWrapper("RecursiveUI:${args.recursion}", Color.Magenta) {
        Text("recursion: ${args.recursion}")

        ScreenAction("Nav to next 'RecursiveUI'", "scope-active: ${vm.isScopeActive()}") {
            navTo_RecursiveUI(args.copy(recursion = args.recursion + 1))
        }

        RecursionButtons(args)

        if (args.recursion > 0) {
            ScreenAction(
                "up 1x",
                "- back to previous recursion"
            ) {
                up()
            }
        }
    }
}

@Composable
private fun RecursionButtons(args: ArgsRecursiveUI) {
    ScreenAction(
        "up ${args.recursion + 1}x",
        "- back to screen 'Screen1UI'\n- skip ${args.recursion} parents"
    ) {
        up(args.recursion + 1)
    }
    ScreenAction(
        "up ${args.recursion + 2}x",
        "- back to screen 'AppUI'\n- skip ${args.recursion + 1} parents"
    ) {
        up(args.recursion + 2)
    }
    ScreenAction(
        "up ${args.recursion + 3}x",
        "- App exit\n- skip ${args.recursion + 2} parents"
    ) {
        up(args.recursion + 3)
    }
}
