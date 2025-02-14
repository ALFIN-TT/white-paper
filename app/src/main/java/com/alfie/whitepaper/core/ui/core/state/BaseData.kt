package com.alfie.whitepaper.core.ui.core.state

open class BaseData(
    var dataState: State = State.INITIAL,
    var error: String? = ""
)