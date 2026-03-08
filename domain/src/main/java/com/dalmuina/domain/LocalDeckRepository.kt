package com.dalmuina.domain

import com.dalmuina.domain.model.DFDeckSummary
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.domain.model.DeckForEdit
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    suspend fun createDeck(name: String, cardIds: Set<Int>): DFResult<Unit, DataBaseError>

    suspend fun updateDeck(deckId: Int, name: String, cardIds: Set<Int>): DFResult<Unit, DataBaseError>


    fun getAllDecks(): Flow<DFResult<List<DFDeckSummary>, DFError>>

    fun getDeckForEdit(deckId: Int): Flow<DFResult<DeckForEdit, DataBaseError>>

}