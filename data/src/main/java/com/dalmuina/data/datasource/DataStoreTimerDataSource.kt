package com.dalmuina.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.dalmuina.core.data.helpers.CrashlyticsLogger
import com.dalmuina.core.data.helpers.safePreferencesCall
import com.dalmuina.domain.TimerDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.model.asEmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
class DataStoreTimerDataSource(
    private val dataStore: DataStore<Preferences>,
    private val logger: CrashlyticsLogger,
) : TimerDataSource {

    companion object {
        private val TOTAL_MILLIS = longPreferencesKey("timer_total_millis")
        private val REMAINING_MILLIS = longPreferencesKey("timer_remaining_millis")
        private val IS_RUNNING = booleanPreferencesKey("timer_is_running")
        private val END_TIME_MILLIS = longPreferencesKey("timer_end_time_millis")
    }

    override fun observeTimerState(): Flow<DFResult<PersistedTimerState, DataError>> {
        return dataStore.data
            .map<Preferences, DFResult<PersistedTimerState, DataError>> { prefs ->
                DFResult.Success(
                    PersistedTimerState(
                        totalMillis = prefs[TOTAL_MILLIS] ?: 0L,
                        remainingMillis = prefs[REMAINING_MILLIS] ?: 0L,
                        isRunning = prefs[IS_RUNNING] ?: false,
                        endTimeMillis = prefs[END_TIME_MILLIS]
                    )
                )
            }
            .catch { e ->
                if (e is IOException) {
                    emit(DFResult.Error(DataError.Preferences.Storage))
                } else {
                    throw e
                }
            }
    }

    override suspend fun saveTimerState(
        state: PersistedTimerState
    ): EmptyResult<DataError> {
        return safePreferencesCall(logger) {
            dataStore.edit { prefs ->
                prefs[TOTAL_MILLIS] = state.totalMillis
                prefs[REMAINING_MILLIS] = state.remainingMillis
                prefs[IS_RUNNING] = state.isRunning

                state.endTimeMillis?.let { prefs[END_TIME_MILLIS] = it }
                    ?: prefs.remove(END_TIME_MILLIS)
            }
        }.asEmptyResult()
    }
}
