package com.dalmuina.core.presentation.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UiEventDispatcherImpl : UiEventDispatcher {
    private val _events =
        MutableSharedFlow<UiEvent>(
            extraBufferCapacity = 1,
        )

    override val events = _events.asSharedFlow()

    override suspend fun dispatch(event: UiEvent) {
        _events.emit(event)
    }
}
