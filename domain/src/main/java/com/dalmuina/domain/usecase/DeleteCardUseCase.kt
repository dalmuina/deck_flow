package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeleteCardUseCase(
    private val repository: LocalCardRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(cardId: Int): DFResult<Unit, DataBaseError> =
        withContext(dispatcher) {
            repository.deleteCard(cardId)
        }
}
