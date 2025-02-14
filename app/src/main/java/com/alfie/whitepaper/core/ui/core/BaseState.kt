package com.alfie.basicnetworkapplication.ui.screens.core

import com.alfie.whitepaper.core.ui.core.state.State




/**
 * A base UI state
 */
open class BaseState(
    var message: String? = "",
    var errorCode: String = "",
    var code: Int? = -1,
    var uiState: State = State.INITIAL
)