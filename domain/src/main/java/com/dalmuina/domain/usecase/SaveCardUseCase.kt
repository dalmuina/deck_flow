package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveCardUseCase(
    private val repository: LocalCardRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(card: DFCardDomain): DFResult<Int, DataBaseError> =
        withContext(dispatcher) {
            repository.saveCard(card)
        }
}
