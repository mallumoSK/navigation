import androidx.compose.material.*
import androidx.compose.ui.window.*
import tk.mallumo.compose.navigation.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            NavigationRoot(Node.AppUI)
//            NavigationRoot(Node.ChildTestHomeUI)
        }
    }
}
