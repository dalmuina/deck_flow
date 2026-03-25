package com.dalmuina.domain.usecase

import com.dalmuina.domain.TimerRepository
import com.dalmuina.domain.model.PersistedTimerState
import kotlinx.coroutines.flow.Flow

class ObserveTimerStateUseCase(
    private val repository: TimerRepository,
) {
    operator fun invoke(): Flow<PersistedTimerState> =
        repository.observeTimerState()
}