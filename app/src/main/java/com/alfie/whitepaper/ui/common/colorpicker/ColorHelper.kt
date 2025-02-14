package com.alfie.whitepaper.ui.common.colorpicker

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.toRect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource, setOffset: (Offset) -> Unit
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)?.pressPosition?.let(setOffset)
        }
    }
}


fun Modifier.emitDragGesture(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val scope = rememberCoroutineScope()

    pointerInput(Unit) {
        detectDragGestures { input, _ ->
            scope.launch {
                interactionSource.emit(PressInteraction.Press(input.position))
            }
        }
    }
}

fun DrawScope.drawBitmap(
    bitmap: Bitmap, panel: RectF
) {
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap, null, panel.toRect(), null
        )
    }
}


@Composable
internal fun ChangeVisibility(
    isVisible: Boolean, density: Density, content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(visible = isVisible, enter = slideInVertically {
        // Slide in from 40 dp from the top.
        with(density) {
            -40.dp.roundToPx()
        }
    } + expandVertically(
        // Expand from the top.
        expandFrom = Alignment.Top
    ) + fadeIn(
        // Fade in with the initial alpha of 0.3f.
        initialAlpha = 0.3f
    ), exit = slideOutVertically() + shrinkVertically(
        shrinkTowards = Alignment.Top
    ) + fadeOut(), content = content)
}

fun Color.convertToOldColor(): Int {
    val color = this.toArgb()
    return android.graphics.Color.argb(
        color.alpha, color.red, color.green, color.blue
    )
}

val ColorSaver = run {
    val redKey = "Red"
    val greenKey = "Green"
    val blueKey = "Blue"
    mapSaver(save = { mapOf(redKey to it.red, greenKey to it.green, blueKey to it.blue) },
        restore = {
            Color(
                red = it[redKey] as Float,
                green = it[greenKey] as Float,
                blue = it[blueKey] as Float
            )
        })
}

fun Color.lighter(factor: Float = 1f) =
    Color(ColorUtils.blendARGB(this.toArgb(), Color.White.toArgb(), factor))

fun Color.darker(factor: Float = 1f) =
    Color(ColorUtils.blendARGB(this.toArgb(), Color.Black.toArgb(), factor))