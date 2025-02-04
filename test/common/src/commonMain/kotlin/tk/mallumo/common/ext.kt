package tk.mallumo.common

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import tk.mallumo.compose.navigation.*


@Composable
fun ContentWrapper(screen: String, background: Color, body: @Composable ColumnScope.() -> Unit) {
    val nav = LocalNavigation.current

    var infoText by remember { mutableStateOf(AnnotatedString("")) }

    Column(
        Modifier
            .fillMaxWidth()
            .background(background.copy(alpha = 0.1F))
            .padding(8.dp)
    ) {
        ScreenTitle(screen) {
            infoText = buildNavInfoText(nav)
        }
        Spacer(Modifier.size(16.dp))
        AnimatedVisibility(infoText.isNotEmpty()) {
            Text(infoText)
        }
        Spacer(Modifier.size(16.dp))
        body()
    }

    LaunchedEffect(Unit) {
        infoText = buildNavInfoText(nav)
    }
}

private fun buildNavInfoText(nav: Navigation) = buildAnnotatedString {
    appendLine("isRootNode", nav.testing.isRootNode)
    appendLine("currentNodeId", nav.testing.currentNodeId)
    appendLine("childNavigationIds", nav.testing.childNavigationIds)
    appendLine("currentNavigationNodeIds", nav.testing.currentNavigationNodeIds)
    appendLine("currentNodeVmIds", nav.testing.currentNodeVmIds)
    appendLine("currentNavigationBackStackIds", nav.testing.currentNavigationBackStackIds)
    appendLine("currentNodeBackPressCallbackIds", nav.testing.currentNodeBackPressCallbackIds)
}

private fun AnnotatedString.Builder.appendLine(title: String, msg: Boolean) {
    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    ) {
        append("$title:\n")
    }
    withStyle(
        style = SpanStyle(
            fontSize = 12.sp
        )
    ) {
        append("- $msg")
        append("\n")
    }
}

private fun AnnotatedString.Builder.appendLine(title: String, msg: String) {
    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    ) {
        append("$title:\n")
    }
    withStyle(
        style = SpanStyle(
            fontSize = 12.sp
        )
    ) {
        if (msg.isEmpty()) append("- none")
        else append("- $msg")

        append("\n")
    }
}

private fun AnnotatedString.Builder.appendLine(title: String, msg: Collection<String>) {
    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    ) {
        append("$title:\n")
    }
    withStyle(
        style = SpanStyle(
            fontSize = 12.sp
        )
    ) {
        if (msg.isEmpty()) append("- none")
        else append("- ${msg.joinToString("\n- ")}")

        append("\n")
    }

}


@Composable
fun ScreenTitle(name: String, onReload: () -> Unit) {
    Row(Modifier.padding(4.dp)) {
        Button(onReload) {
            Text("Reload info")
        }
        Text(
            text = "Screen of $name",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()

        )
    }

}

@Composable
fun ScreenAction(title: String, description: String? = null, onAction: Navigation.() -> Unit) {
    val nav = LocalNavigation.current
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onAction(nav) }
            .padding(16.dp)) {
        Column(Modifier.weight(1F)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = title,
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        )
    }

}
