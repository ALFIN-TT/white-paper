package com.alfie.whitepaper.ui.screen.canvas.state

import com.alfie.basicnetworkapplication.ui.screens.core.BaseState
import com.alfie.whitepaper.core.ui.core.StateHolder
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad

class CanvasUserState {
    var canvasState: StateHolder<CanvasState> = StateHolder(CanvasState())
    var drawCanvasPayLoadToString: (DrawCanvasPayLoad) -> String = { _ -> "" }
}


data class CanvasState(
    var drawCanvasPayLoad: DrawCanvasPayLoad = DrawCanvasPayLoad()
) : BaseState()


data class CanvasUserEvents(
    var onSave: (DrawCanvasPayLoad, (() -> Unit)) -> Unit = { _, _ -> },
    var onExport: (DrawCanvasPayLoad, (() -> Unit)) -> Unit = { _, _ -> }
)