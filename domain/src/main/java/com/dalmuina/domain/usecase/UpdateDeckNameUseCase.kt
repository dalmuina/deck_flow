package com.dalmuina.domain.usecase

import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class UpdateDeckNameUseCase(
    private val repository: DeckLocalDataSource,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        deckId: Int,
        name: String,
    ): EmptyResult<DataError> =
        withContext(dispatcher) {
            repository.updateDeckName(deckId, name)
        }
}
