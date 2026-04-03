package com.dalmuina.core.presentation

sealed interface UiEvent {
    data class ShowSnackBar(
        val messageRes: Int,
        val actionLabelRes: Int? = null
    ) : UiEvent
}
