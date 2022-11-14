package tk.mallumo.compose.navigation

import android.os.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import kotlin.math.*


private enum class SwipeState {
    START, END
}

class SwipeToDismissOverlayState(
    val isEnabled: MutableState<Boolean>,
    val arrowAngle: Float,
    val arrowColor: Color? = null,
    val onDismiss: () -> Unit
)

@Suppress("unused")
@Composable
fun rememberSwipeToDismissOverlayState(
    isEnabled: MutableState<Boolean> = mutableStateOf(Build.VERSION.SDK_INT < 29),
    arrowAngle: Float = 100F,
    arrowColor: Color? = null,
    onDismiss: () -> Unit
) = remember {
    SwipeToDismissOverlayState(
        isEnabled = isEnabled,
        arrowAngle = arrowAngle,
        arrowColor = arrowColor,
        onDismiss = onDismiss
    )
}

@Suppress("unused")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissOverlay(
    modifier: Modifier = Modifier,
    state: SwipeToDismissOverlayState,
    body: @Composable BoxScope.() -> Unit
) {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val sweepableState = rememberSwipeableState(SwipeState.START,
        confirmStateChange = { newState ->
            state.onDismiss.takeIf { newState == SwipeState.END }?.invoke()
            false
        })


    val dest = LocalDensity.current

    val dimensions = remember {
        val end = with(dest) { (screenWidth.toPx() / 8F) }
        val start = with(dest) { -(end / 4F).dp.toPx() }
        start to end

    }

    val anchors = remember {
        mapOf(
            dimensions.first to SwipeState.START,
            dimensions.second to SwipeState.END
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeable(
                state = sweepableState,
                enabled = state.isEnabled.value,
                anchors = anchors,
                thresholds = { _, _ -> FixedThreshold(dimensions.second.dp) },
                resistance = ResistanceConfig(
                    dimensions.second,
                    dimensions.second,
                    dimensions.second
                ),
                orientation = Orientation.Horizontal
            )
    ) {
        body()
        if (state.isEnabled.value) {
            DismissLine(
                modifier = Modifier.align(Alignment.CenterStart),
                state = state,
                offset = sweepableState.offset,
                offsetMax = dimensions.second
            )
        }
    }
}

@Composable
private fun DismissLine(
    modifier: Modifier,
    state: SwipeToDismissOverlayState,
    offset: State<Float>,
    offsetMax: Float
) {
    val dest = LocalDensity.current

    val onePercent = remember {
        offsetMax / state.arrowAngle
    }

    val pathColor = state.arrowColor ?: contentColorFor(backgroundColor = LocalContentColor.current)

    Canvas(modifier = modifier
        .size(24.dp)
        .offset { IntOffset(offset.value.roundToInt(), 0) }) {
        val width = size.width
        val height = size.height
        val center = height / 2F

        if (offset.value > 0F) {
            val lineOffset = ((offset.value / dest.density) / onePercent) / 100F

            val path = Path().apply {
                moveTo(lineOffset * width, 0F)
                lineTo(0F, center)
                lineTo(lineOffset * width, height)
            }
            drawPath(
                path = path,
                alpha = 0.9F,
                color = pathColor,
                blendMode = BlendMode.SrcOver,
                style = Stroke(
                    width = 5F
                )
            )
        }
    }
}
