package com.dalmuina.domain.usecase

import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateCardUseCase(
    private val repository: CardLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(card: DFCardDomain): DFResult<Int, DataError.Local> =
        withContext(dispatcher) {
            repository.updateCard(card)
        }
}
