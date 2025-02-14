package com.alfie.whitepaper.core.ui.core.state

sealed class Resource<T>(
    val data: T? = null, val message: String? = null
) {
    class Loading<T>(data: T?) : Resource<T>(data = data)
    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(message: String?) : Resource<T>(message = message)
}