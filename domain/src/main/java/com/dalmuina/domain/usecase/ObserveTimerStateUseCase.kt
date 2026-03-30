package com.dalmuina.domain.usecase

import com.dalmuina.domain.TimerRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.PersistedTimerState
import kotlinx.coroutines.flow.Flow

class ObserveTimerStateUseCase(
    private val repository: TimerRepository,
) {
    operator fun invoke(): Flow<DFResult<PersistedTimerState, DataError.Preferences>> =
        repository.observeTimerState()
}