package com.dalmuina.domain.usecase

import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetCardByIdUseCase(
    private val repository: CardLocalDataSource,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(cardId: Int): DFResult<CardDomain, DataError> =
        withContext(dispatcher) {
            repository.getCardById(cardId)
        }
}
