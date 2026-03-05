package com.dalmuina.domain

import com.dalmuina.domain.model.DFDeckSummary
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    suspend fun createDeck(name: String, cardIds: Set<Int>): DFResult<Unit, DataBaseError>

    fun getAllDecks(): Flow<DFResult<List<DFDeckSummary>, DFError>>


}