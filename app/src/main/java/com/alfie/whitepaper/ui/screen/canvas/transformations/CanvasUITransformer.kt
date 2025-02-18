package com.alfie.whitepaper.ui.screen.canvas.transformations

import com.alfie.basicnetworkapplication.ui.screens.core.Transformer
import com.alfie.whitepaper.ui.screen.canvas.state.CanvasUIState
import com.alfie.whitepaper.ui.screen.canvas.viewmodel.CanvasUIViewModel

class CanvasUIViewModelStateTransform : Transformer<CanvasUIViewModel, CanvasUIState> {
    override fun transform(input: CanvasUIViewModel) = with(input) {
        val userState = CanvasUIState()
        userState.canvasState = input.canvasState.value
        userState.drawCanvasPayLoadToString = {
            input.drawCanvasPayLoadToString(it)
        }
        userState
    }
}