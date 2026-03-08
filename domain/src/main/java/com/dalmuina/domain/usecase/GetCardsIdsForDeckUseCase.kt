package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.domain.model.DeckForEdit
import kotlinx.coroutines.flow.Flow

class GetCardsIdsForDeckUseCase(
    private val repository: LocalDeckRepository
) {
    operator fun invoke(deckId: Int): Flow<DFResult<DeckForEdit, DataBaseError>> =
        repository.getDeckForEdit(deckId)
}
