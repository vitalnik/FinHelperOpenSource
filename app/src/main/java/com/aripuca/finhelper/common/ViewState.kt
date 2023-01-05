package com.aripuca.finhelper.common

import kotlinx.coroutines.flow.MutableStateFlow

sealed class ViewState<out T : Any> {

    object Initial : ViewState<Nothing>()

    object Loading : ViewState<Nothing>()

    object Empty : ViewState<Nothing>()

    data class Error(val errorCode: String) : ViewState<Nothing>()

    data class Populated<T : Any>(private val value: T) : ViewState<T>() {
        operator fun invoke(): T = value
    }
}

fun <T : Any, R : Any?> ViewState<T>?.withState(block: (T) -> R?) =
    this?.let {
        when (it) {
            is ViewState.Populated -> {
                block(it())
            }
            else -> null
        }
    }

fun <T : Any, R : Any?> MutableStateFlow<ViewState<T>>.withState(block: (T) -> R?) =
    when (val value = this.value) {
        is ViewState.Populated -> {
            block(value())
        }
        else -> null
    }

