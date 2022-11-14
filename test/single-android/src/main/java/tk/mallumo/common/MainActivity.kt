package tk.mallumo.common

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.material.*
import tk.mallumo.compose.navigation.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NavigationRoot(Node.AppUI)
            }
        }
    }
}
