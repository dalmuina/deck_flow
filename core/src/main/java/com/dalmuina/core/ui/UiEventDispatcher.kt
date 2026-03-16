package com.dalmuina.core.ui

import kotlinx.coroutines.flow.SharedFlow

interface UiEventDispatcher {
    val events: SharedFlow<UiEvent>
    suspend fun dispatch(event: UiEvent)
}
