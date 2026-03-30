package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetCardByIdUseCase(
    private val repository: LocalCardRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(cardId: Int): DFResult<DFCardDomain, DataError.Local> =
        withContext(dispatcher) {
            repository.getCardById(cardId)
        }
}
