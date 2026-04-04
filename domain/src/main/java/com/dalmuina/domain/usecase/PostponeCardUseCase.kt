package com.dalmuina.domain.usecase

import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PostponeCardUseCase(
    private val repository: CardLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(cardId: Int): DFResult<Int, DataError.Local> =
        withContext(dispatcher) {
            repository.postponeCard(cardId)
        }
}
