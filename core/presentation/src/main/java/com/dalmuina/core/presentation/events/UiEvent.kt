package com.dalmuina.core.presentation.events

sealed interface UiEvent {
    data class ShowSnackBar(
        val messageRes: Int,
        val actionLabelRes: Int? = null
    ) : UiEvent
}
