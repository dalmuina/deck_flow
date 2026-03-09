package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AddCardToDeckUseCase(
    private val repository: LocalDeckRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(deckId: Int, cardId:Int): DFResult<Unit, DataBaseError> =
        withContext(dispatcher) {
            repository.addCardToDeck(deckId,cardId)
        }
}