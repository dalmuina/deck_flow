package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveCardUseCase(
    private val repository: LocalCardRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(card: DFCard): DFResult<Unit, DFError> =
        withContext(dispatcher) {
            repository.saveCard(card)
        }
}