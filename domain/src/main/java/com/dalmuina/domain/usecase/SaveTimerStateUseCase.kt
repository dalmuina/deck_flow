package com.dalmuina.domain.usecase

import com.dalmuina.domain.TimerRepository
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import com.dalmuina.domain.model.PersistedTimerState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveTimerStateUseCase(
    private val repository: TimerRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        state: PersistedTimerState
    ): EmptyResult<DataError.Preferences> = withContext(dispatcher) {
        repository.saveTimerState(state)
    }
}