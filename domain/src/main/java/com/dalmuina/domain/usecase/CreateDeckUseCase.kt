package com.dalmuina.domain.usecase

import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CreateDeckUseCase(
    private val repository: DeckLocalDataSource,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        name: String,
        cardIds: List<Int>,
    ): EmptyResult<DataError> =
        withContext(dispatcher) {
            repository.createDeck(name, cardIds)
        }
}