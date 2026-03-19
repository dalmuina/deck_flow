package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import kotlinx.coroutines.flow.Flow

class GetAllCardsUseCase(
    private val repository: LocalCardRepository
) {
    operator fun invoke(): Flow<DFResult<List<DFCardDomain>, DFError>> =
        repository.getAllCards()
}
