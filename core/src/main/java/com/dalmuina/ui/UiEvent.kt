package com.dalmuina.ui

sealed interface UiEvent {
    data class ShowSnackBar(
        val messageRes: Int,
        val actionLabelRes: Int? = null
    ) : UiEvent
}
