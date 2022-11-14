package tk.mallumo.android

//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.*
//import org.junit.*
import androidx.compose.material.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.*
import org.junit.*
import tk.mallumo.compose.navigation.*

class TestOfNavigation {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TemplateActivity>()

    @Test
    fun test_AppUI() {
        composeTestRule.apply {
            setContent {
                MaterialTheme {
                    NavigationRoot(Node.AppUI)
                }
            }
            onNodeWithText("Open Screen1UI").performClick()
            onNodeWithText("Screen1UI").assertIsDisplayed()
            onNodeWithText("Back to AppUI").performClick()
            onNodeWithText("Open Screen1UI").assertIsDisplayed()
        }


    }
}
