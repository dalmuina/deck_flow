package com.dalmuina.domain

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import com.dalmuina.domain.model.PersistedTimerState
import kotlinx.coroutines.flow.Flow

interface TimerDataSource {
    fun observeTimerState(): Flow<DFResult<PersistedTimerState, DataError>>
    suspend fun saveTimerState(state: PersistedTimerState): EmptyResult<DataError>
}
