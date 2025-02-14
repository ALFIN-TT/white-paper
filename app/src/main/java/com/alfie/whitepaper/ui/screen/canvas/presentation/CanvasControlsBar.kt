package com.alfie.whitepaper.ui.screen.canvas.presentation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfie.whitepaper.R
import com.alfie.whitepaper.ui.common.BrushSize
import com.alfie.whitepaper.ui.common.canvas.DrawController
import com.alfie.whitepaper.ui.common.colorpicker.lighter
import com.alfie.whitepaper.ui.theme.montserratFamily

@Composable
fun ControlsBar(
    modifier: Modifier,
    drawController: DrawController,
    onSaveClick: () -> Unit,
    onResetClick: () -> Unit,
    onColorClick: () -> Unit,
    onBgColorClick: () -> Unit,
    onSizeClick: () -> Unit,
    undoVisibility: MutableState<Boolean>,
    redoVisibility: MutableState<Boolean>,
    colorValue: MutableState<Color>,
    bgColorValue: MutableState<Color>,
    sizeValue: MutableState<Int>,
) {
    Row(
        modifier = modifier.padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MenuItems(R.drawable.ic_brush, "Brush Size", MaterialTheme.colorScheme.primary) {
            onSizeClick()
        }
        MenuItems(
            R.drawable.ic_round, "Brush color", colorValue.value, true
        ) {
            onColorClick()
        }
        MenuItems(
            R.drawable.ic_round,
            "Canvas Color",
            bgColorValue.value,
            true  //bgColorValue.value == MaterialTheme.colorScheme.background
        ) {
            onBgColorClick()
        }
        MenuItems(
            R.drawable.ic_undo,
            "Undo",
            if (undoVisibility.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        ) {
            if (undoVisibility.value) drawController.unDo()
        }
        MenuItems(
            R.drawable.ic_redo,
            "Redo",
            if (redoVisibility.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        ) {
            if (redoVisibility.value) drawController.reDo()
        }
        MenuItems(
            R.drawable.ic_refresh,
            "Reset Canvas",
            if (redoVisibility.value || undoVisibility.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        ) {
            onResetClick.invoke()
        }
        MenuItems(
            R.drawable.save,
            "Save",
            if (undoVisibility.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        ) {
            if (undoVisibility.value) onSaveClick()
        }
    }
}

@Composable
fun RowScope.MenuItems(
    @DrawableRes resId: Int,
    desc: String,
    colorTint: Color,
    border: Boolean = false,
    onClick: () -> Unit
) {
    val modifier = Modifier.size(24.dp)
    IconButton(
        onClick = onClick, modifier = Modifier.weight(1f, true)
    ) {
        Icon(
            painterResource(id = resId),
            contentDescription = desc,
            tint = colorTint,
            modifier = if (border) modifier.border(
                0.9.dp,
                Color.Black,
                shape = CircleShape
            ) else modifier
        )
    }
}

@Composable
fun BrushSizeSlider(
    isVisible: Boolean,
    max: Float = 200f,
    progress: Int = max.toInt(),
    progressColor: Color,
    thumbColor: Color,
    onProgressChanged: (Int) -> Unit
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            val sliderPosition = remember { mutableIntStateOf(progress) }

            Text(
                text = stringResource(id = R.string.str_brush_size),
                modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = montserratFamily,
                fontWeight = FontWeight.SemiBold
            )
            BrushSize(
                brushSize = sliderPosition.intValue.toFloat(),
                brushColor = progressColor
            )
            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                valueRange = 0f..max,
                value = sliderPosition.intValue.toFloat(),
                onValueChange = {
                    sliderPosition.intValue = it.toInt()
                    onProgressChanged(sliderPosition.intValue)
                },
                colors = SliderDefaults.colors(
                    thumbColor = thumbColor,
                    activeTrackColor = progressColor,
                    inactiveTrackColor = progressColor.lighter(.5f),
                )
            )
        }
    }
}