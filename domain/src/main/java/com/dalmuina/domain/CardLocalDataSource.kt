package com.dalmuina.domain

import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DailyStatsDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface CardLocalDataSource {
    suspend fun saveCard(card: CardDomain): DFResult<Int, DataError.Local>
    suspend fun updateCard(card: CardDomain): DFResult<Int, DataError.Local>
    suspend fun completeCard(cardId: Int, spentMillis: Long): DFResult<Int, DataError.Local>
    suspend fun postponeCard(cardId: Int): DFResult<Int, DataError.Local>

    fun getAllCards(): Flow<DFResult<List<CardDomain>, DataError.Local>>
    suspend fun getCardById(cardId: Int): DFResult<CardDomain, DataError.Local>
    suspend fun deleteCard(cardId: Int): EmptyResult<DataError.Local>
    fun getDailyStatsForCard(cardId: Int, fromDay: Long, toDay: Long) : Flow<DFResult<List<DailyStatsDomain>, DataError.Local>>
 }
