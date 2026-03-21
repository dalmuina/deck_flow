package com.dalmuina.domain

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.PreferencesError
import kotlinx.coroutines.flow.Flow

interface SelectedDeckRepository {

    val selectedDeckId: Flow<Int?>

    suspend fun setSelectedDeck(id: Int): DFResult<Unit, PreferencesError>
}