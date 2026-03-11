package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeck
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

class GetDeckByIdUseCase(
    private val repository: LocalDeckRepository,
) {
    operator fun invoke(deckId: Int): Flow<DFResult<DFDeck, DataBaseError>> =
        repository.getDeckById(deckId)
}
