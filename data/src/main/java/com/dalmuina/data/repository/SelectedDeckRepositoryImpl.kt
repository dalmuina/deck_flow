package com.dalmuina.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.dalmuina.domain.SelectedDeckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SelectedDeckRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): SelectedDeckRepository {
    private val SELECTED_DECK = intPreferencesKey("selected_deck")

    override val selectedDeckId: Flow<Int?> =
        dataStore.data.map { prefs ->
            prefs[SELECTED_DECK]
        }

    override suspend fun setSelectedDeck(id: Int) {
        dataStore.edit { prefs ->
            prefs[SELECTED_DECK] = id
        }
    }
}