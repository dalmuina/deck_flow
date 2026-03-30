package com.dalmuina.domain

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import com.dalmuina.domain.model.PersistedTimerState
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    fun observeTimerState(): Flow<DFResult<PersistedTimerState, DataError.Preferences>>
    suspend fun saveTimerState(state: PersistedTimerState): EmptyResult<DataError.Preferences>
}