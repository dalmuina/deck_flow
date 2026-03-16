package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PostponeCardUseCase(
    private val repository: LocalCardRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(cardId: Int): DFResult<Int, DataBaseError> =
        withContext(dispatcher) {
            repository.postponeCard(cardId)
        }
}
