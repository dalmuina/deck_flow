package com.dalmuina.domain

import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DailyStatsDomain
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface CardLocalDataSource {
    suspend fun saveCard(card: CardDomain): DFResult<Int, DataError>

    suspend fun updateCard(card: CardDomain): DFResult<Int, DataError>

    suspend fun completeCard(
        cardId: Int,
        spentMillis: Long,
    ): DFResult<Int, DataError>

    suspend fun postponeCard(cardId: Int): DFResult<Int, DataError>

    fun getAllCards(): Flow<DFResult<List<CardDomain>, DataError>>

    suspend fun getCardById(cardId: Int): DFResult<CardDomain, DataError>

    suspend fun deleteCard(cardId: Int): EmptyResult<DataError>

    fun getDailyStatsForCard(
        cardId: Int,
        fromDay: Long,
        toDay: Long,
    ): Flow<DFResult<List<DailyStatsDomain>, DataError>>
}
