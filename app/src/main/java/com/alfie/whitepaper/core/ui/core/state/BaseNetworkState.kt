package com.alfie.whitepaper.core.ui.core.state

import androidx.compose.runtime.Composable

/**
 * Data state for the app
 */
enum class State {
    INITIAL,
    LOADING,
    ERROR,
    SUCCESS
}

/***
 * A composable state resolution for different state of the data over the long running tasks.
 * @param loading
 * @param success
 * @param error
 */
fun State.resolveState(
    initial: () -> Unit = {},
    loading: () -> Unit = {},
    success: () -> Unit = {},
    error: () -> Unit = {}
) {
    when (this) {
        State.INITIAL -> initial.invoke()
        State.LOADING -> loading.invoke()
        State.SUCCESS -> success.invoke()
        State.ERROR -> error.invoke()
    }
}

/***
 * A composable state resolution for different state of the data over the long running tasks.
 * @param loading
 * @param success
 * @param error
 */
@Composable
fun State.ResolveState(
    initial: @Composable () -> Unit = {},
    loading: @Composable () -> Unit = {},
    success: @Composable () -> Unit = {},
    error: @Composable () -> Unit = {}
) {
    when (this) {
        State.INITIAL -> initial.invoke()
        State.LOADING -> loading.invoke()
        State.SUCCESS -> success.invoke()
        State.ERROR -> error.invoke()
    }
}
