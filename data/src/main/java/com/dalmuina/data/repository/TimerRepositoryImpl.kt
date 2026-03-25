package com.dalmuina.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.dalmuina.domain.TimerRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PersistedTimerState
import com.dalmuina.domain.model.PreferencesError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TimerRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : TimerRepository {

    companion object {
        private val TOTAL_MILLIS = longPreferencesKey("timer_total_millis")
        private val REMAINING_MILLIS = longPreferencesKey("timer_remaining_millis")
        private val IS_RUNNING = booleanPreferencesKey("timer_is_running")
        private val END_TIME_MILLIS = longPreferencesKey("timer_end_time_millis")
    }

    override fun observeTimerState(): Flow<PersistedTimerState> {
        return dataStore.data.map { prefs ->
            PersistedTimerState(
                totalMillis = prefs[TOTAL_MILLIS] ?: 0L,
                remainingMillis = prefs[REMAINING_MILLIS] ?: 0L,
                isRunning = prefs[IS_RUNNING] ?: false,
                endTimeMillis = prefs[END_TIME_MILLIS]
            )
        }
    }

    override suspend fun saveTimerState(
        state: PersistedTimerState
    ): DFResult<Unit, PreferencesError> {
        return try {
            dataStore.edit { prefs ->
                prefs[TOTAL_MILLIS] = state.totalMillis
                prefs[REMAINING_MILLIS] = state.remainingMillis
                prefs[IS_RUNNING] = state.isRunning

                val endTimeMillis = state.endTimeMillis
                endTimeMillis?.let {
                    prefs[END_TIME_MILLIS] = it
                } ?: prefs.remove(END_TIME_MILLIS)
            }
            DFResult.Success(Unit)
        } catch (e: IOException) {
            DFResult.Error(PreferencesError.Storage)
        }
    }
}