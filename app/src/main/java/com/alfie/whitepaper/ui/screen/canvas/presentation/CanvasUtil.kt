package com.alfie.whitepaper.ui.screen.canvas.presentation

import android.content.Context
import androidx.compose.runtime.MutableState
import com.alfie.whitepaper.core.utils.shareFile
import com.alfie.whitepaper.data.model.SaveOption
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad
import com.alfie.whitepaper.ui.common.canvas.DrawController

fun saveOrShareImageFile(
    canvasImageSaveOption: MutableState<Int>,
    drawController: DrawController,
    saveOption: SaveOption
) {
    canvasImageSaveOption.value = saveOption.type
    drawController.saveBitmap()
}

suspend fun shareProject(
    drawController: DrawController,
    context: Context,
    drawCanvasPayLoadToString: (DrawCanvasPayLoad) -> String = { _ -> "" }
) {
    val thumbnail = drawController.getDrawingAsBase64()
    shareFile(
        drawCanvasPayLoadToString(
            drawController.exportPath().apply {
                this.thumbnail = thumbnail
            }
        ), context
    )
}
