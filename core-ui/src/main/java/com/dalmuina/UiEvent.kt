package com.dalmuina

sealed interface UiEvent {
    data class ShowSnackBar(
        val message: String,
        val actionLabel: String? = null
    ) : UiEvent
}
