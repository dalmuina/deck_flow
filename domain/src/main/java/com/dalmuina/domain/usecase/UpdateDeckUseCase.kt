package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateDeckUseCase(
    private val repository: LocalDeckRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        deckId: Int,
        name: String,
        cardIds: Set<Int>,
    ): DFResult<Unit, DataBaseError> =
        withContext(dispatcher) {
            repository.updateDeck(deckId, name, cardIds)
        }
}