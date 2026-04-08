package com.dalmuina.domain.usecase

import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveCardUseCase(
    private val repository: CardLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(card: CardDomain): DFResult<Int, DataError> =
        withContext(dispatcher) {
            repository.saveCard(card)
        }
}
