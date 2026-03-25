package com.dalmuina.domain

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.model.PreferencesError
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    fun observeTimerState(): Flow<PersistedTimerState>
    suspend fun saveTimerState(state: PersistedTimerState): DFResult<Unit, PreferencesError>
}