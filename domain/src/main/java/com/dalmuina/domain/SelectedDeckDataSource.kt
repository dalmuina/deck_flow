package com.dalmuina.domain

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface SelectedDeckDataSource {

    val selectedDeckId: Flow<DFResult<Int?, DataError.Preferences>>

    suspend fun setSelectedDeck(id: Int): EmptyResult<DataError.Preferences>
}
