package com.alfie.whitepaper.ui.common.colorpicker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import android.graphics.Color as AndroidColor

@Composable
fun DrawColorPicker(
    currentColor: Color = Color.Blue,
    isVisible: Boolean = false,
    clickedColor: (Color) -> Unit
) {

    val density = LocalDensity.current
    ChangeVisibility(isVisible, density) {
        val hsv = remember {
            val hsv = floatArrayOf(0f, 0f, 0f)
            AndroidColor.colorToHSV(currentColor.toArgb(), hsv)
            mutableStateOf(
                Triple(hsv[0], hsv[1], hsv[2])
            )
        }
        val backgroundColor = remember(hsv.value) {
            mutableStateOf(
                Color.hsv(
                    hsv.value.first,
                    hsv.value.second,
                    hsv.value.third
                )
            )
        }

        val configuration = LocalConfiguration.current
        val width = (configuration.screenWidthDp / 2) - 12
        val height = configuration.screenWidthDp / 4

        ConstraintLayout(
            Modifier.padding(horizontal = 20.dp)
        ) {
            val (colorGradientBox, colorHueSlider, colorBox) = createRefs()
            DrawColorGradientBox(
                modifier = Modifier
                    .constrainAs(colorGradientBox) {
                        start.linkTo(parent.start)
                        end.linkTo(colorBox.start, 3.dp)
                        top.linkTo(parent.top, 12.dp)
                    }
                    .width((width + 4).dp)
                    .height(height.dp),
                hue = hsv.value.first
            ) { sat, value ->
                hsv.value = Triple(hsv.value.first, sat, value)
                clickedColor.invoke(backgroundColor.value)
            }
            Box(
                modifier = Modifier
                    .constrainAs(colorBox) {
                        start.linkTo(colorGradientBox.end, 3.dp)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top, 12.dp)
                    }
                    .width((width - 4).dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor.value)
            )
            HueBar(modifier = Modifier.constrainAs(colorHueSlider) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(colorBox.bottom, 12.dp)
                bottom.linkTo(parent.bottom, 12.dp)
            }) { hue ->
                hsv.value = Triple(hue, hsv.value.second, hsv.value.third)
                clickedColor.invoke(backgroundColor.value)
            }
        }
    }
}

@Composable
fun HueBar(
    modifier: Modifier = Modifier,
    setColor: (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressOffset = remember {
        mutableStateOf(Offset.Zero)
    }
    Canvas(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(25))
            .emitDragGesture(interactionSource)
    ) {
        val drawScopeSize = size
        val bitmap =
            Bitmap.createBitmap(
                size.width.toInt(),
                size.height.toInt(),
                Bitmap.Config.ARGB_8888
            )
        val hueCanvas = Canvas(bitmap)

        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val hueColors = IntArray((huePanel.width()).toInt())
        var hue = 0f
        for (i in hueColors.indices) {
            hueColors[i] = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
            hue += 360f / hueColors.size
        }

        val linePaint = Paint()
        linePaint.strokeWidth = 0F
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
        }

        drawBitmap(
            bitmap = bitmap,
            panel = huePanel
        )

        fun pointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x = when {
                pointX < huePanel.left -> 0F
                pointX > huePanel.right -> width
                else -> pointX - huePanel.left
            }
            return x * 360f / width
        }

        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            pressOffset.value = Offset(pressPos, 0f)
            val selectedHue = pointToHue(pressPos)
            setColor(selectedHue)
        }

        drawCircle(
            Color.White,
            radius = size.height / 2,
            center = Offset(pressOffset.value.x, size.height / 2),
            style = Stroke(
                width = 2.dp.toPx()
            )
        )
    }
}