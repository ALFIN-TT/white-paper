package com.alfie.whitepaper.ui.screen.canvas.state

import com.alfie.whitepaper.core.ui.core.StateHolder
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad

class CanvasUIState {
    var canvasState: StateHolder<CanvasState> = StateHolder(CanvasState())
    var drawCanvasPayLoadToString: (DrawCanvasPayLoad) -> String = { _ -> "" }
}


data class CanvasState(
    var drawCanvasPayLoad: DrawCanvasPayLoad = DrawCanvasPayLoad()
)

sealed interface CanvasEvents {
    data class SaveAndShareWithPayLoad(val payLoad: DrawCanvasPayLoad, val onSave: () -> Unit) : CanvasEvents
    data class SaveAndShare(val onSave: () -> Unit) : CanvasEvents
    data class Export(val payLoad: DrawCanvasPayLoad, val onExport: () -> Unit) : CanvasEvents
}
