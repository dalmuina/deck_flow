package com.dalmuina.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.dalmuina.domain.SelectedDeckRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PreferencesError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SelectedDeckRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SelectedDeckRepository {

    private val SELECTED_DECK = intPreferencesKey("selected_deck")

    override val selectedDeckId: Flow<Int?> =
        dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs ->
                prefs[SELECTED_DECK]
            }

    override suspend fun setSelectedDeck(id: Int): DFResult<Unit, PreferencesError> =
        try {
            dataStore.edit { prefs ->
                prefs[SELECTED_DECK] = id
            }
            DFResult.Success(Unit)
        } catch (t: Throwable) {
            DFResult.Error(PreferencesError.Unknown(t))
        }
}