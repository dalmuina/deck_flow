package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalCardRepository
import com.dalmuina.domain.model.DFDailyStatsDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.flow.Flow

class GetCardStatsUseCase(
    private val repository: LocalCardRepository,
) {
    operator fun invoke(
        cardId: Int,
        fromDay: Long,
        toDay: Long
    ): Flow<DFResult<List<DFDailyStatsDomain>, DataError.Local>> =
        repository.getDailyStatsForCard(cardId, fromDay, toDay)

}
