package com.dalmuina.domain.usecase

import com.dalmuina.domain.TimerDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.PersistedTimerState
import kotlinx.coroutines.flow.Flow

class ObserveTimerStateUseCase(
    private val repository: TimerDataSource,
) {
    operator fun invoke(): Flow<DFResult<PersistedTimerState, DataError>> =
        repository.observeTimerState()
}