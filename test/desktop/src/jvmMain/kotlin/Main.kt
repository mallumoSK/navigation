import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import tk.mallumo.compose.navigation.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            NavigationRoot(Node.AppUI)
//            NavigationRoot(Node.ChildTestHomeUI)
        }
    }
}
