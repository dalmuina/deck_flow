package com.dalmuina.domain.usecase

import com.dalmuina.domain.TimerRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.model.PreferencesError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveTimerStateUseCase(
    private val repository: TimerRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        state: PersistedTimerState
    ): DFResult<Unit, PreferencesError> = withContext(dispatcher) {
        repository.saveTimerState(state)
    }
}