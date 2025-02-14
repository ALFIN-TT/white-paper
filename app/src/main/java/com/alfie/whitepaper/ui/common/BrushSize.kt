package com.alfie.whitepaper.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun BrushSize(
    modifier: Modifier = Modifier,
    brushSize: Float,
    brushColor: Color = Color.Red
) {
    Canvas(
        modifier = modifier
            .padding(start = 48.dp, end = 48.dp)
            .fillMaxWidth()
            .height(90.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2f
        drawLine(
            start = Offset(0f, centerY), // Starting point in the middle of left edge
            end = Offset(canvasWidth, centerY), // Ending point in the middle of right edge
            color = brushColor,
            strokeWidth = brushSize,
            cap = StrokeCap.Round
        )
    }

}