package tk.mallumo.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import tk.mallumo.compose.navigation.*
import tk.mallumo.compose.navigation.viewmodel.*

@Stable
class ArgsScreen1UI(var showChildNavigation: Boolean = false)

@VM
class Screen1VM : NavigationViewModel() {
    override fun onRelease() {
        println("Screen1VM onRelease")
    }
}

@ComposableNavNode(ArgsScreen1UI::class)
@Composable
fun Screen1UI() {
    val nav = LocalNavigation.current
    nav.viewModel(Screen1VM::class)
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            ContentWrapper("Screen1UI", Color.Blue) {
                ScreenAction("Nav to 'Screen2UI'") {
                    navTo_Screen2UI()
                }

                ScreenAction("Nav to 'RecursiveUI'") {
                    navTo_RecursiveUI()
                }

                ScreenAction(
                    "Nav to 'NewRootUI' + clearTop",
                    "- remove navigation backstack"
                ) {
                    navTo_NewRootUI(clearTop = true)
                }

                ScreenAction("up -> 1x, back into AppUI") {
                    up()
                }

                ScreenAction("up -> 2x, App Exit") {
                    up(2)
                }

                ChildDefinition()
            }
        }
    }
}


@Composable
private fun ChildDefinition() {
    val navArgs = LocalNavigationArgs.current
    val args = navArgs.rememberArgs(ArgsScreen1UI::class)
    var showChild by remember { mutableStateOf(args.showChildNavigation) }

    Button({
        showChild = !showChild
        args.showChildNavigation = showChild
    }) {
        Text(
            if (showChild) "Hide child navigation node"
            else "Show child navigation node"
        )
    }

    if (showChild) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            NavigationChildChild0(Node.NestedChild0_FirstUI)
        }
    }
}

