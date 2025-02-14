package com.alfie.whitepaper.ui.screen.canvas.transformations

import com.alfie.basicnetworkapplication.ui.screens.core.Transformer
import com.alfie.whitepaper.ui.screen.canvas.state.CanvasUserEvents
import com.alfie.whitepaper.ui.screen.canvas.state.CanvasUserState
import com.alfie.whitepaper.ui.screen.canvas.viewmodel.CanvasUIViewModel

class CanvasUIViewModelStateTransform : Transformer<CanvasUIViewModel, CanvasUserState> {
    override fun transform(input: CanvasUIViewModel) = with(input) {
        val userState = CanvasUserState()
        userState.canvasState = input.canvasState.value
        userState.drawCanvasPayLoadToString = {
            input.drawCanvasPayLoadToString(it)
        }
        userState
    }
}


class CanvasUIViewModelEventTransform : Transformer<CanvasUIViewModel, CanvasUserEvents> {
    override fun transform(input: CanvasUIViewModel) = with(input) {
        val userEvents = CanvasUserEvents()
        userEvents.onSave = { project, callback ->
            onSaveRequested(project, callback)
        }
        userEvents.onExport = { project, callback ->
            onExportRequested(project, callback)
        }
        userEvents
    }
}