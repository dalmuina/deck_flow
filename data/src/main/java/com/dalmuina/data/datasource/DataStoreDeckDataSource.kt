package com.dalmuina.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.dalmuina.data.helpers.safePreferencesCall
import com.dalmuina.domain.SelectedDeckDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import com.dalmuina.domain.model.asEmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreDeckDataSource(
    private val dataStore: DataStore<Preferences>
) : SelectedDeckDataSource {

    private val SELECTED_DECK = intPreferencesKey("selected_deck")

    override val selectedDeckId: Flow<DFResult<Int?, DataError.Preferences>> =
        dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs ->
                DFResult.Success(
                    prefs[SELECTED_DECK]
                )
            }

    override suspend fun setSelectedDeck(id: Int): EmptyResult<DataError.Preferences> {
        return safePreferencesCall {
            dataStore.edit { prefs ->
                prefs[SELECTED_DECK] = id
            }
        }.asEmptyResult()
    }
}
