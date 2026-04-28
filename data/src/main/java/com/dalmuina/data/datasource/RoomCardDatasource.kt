package com.dalmuina.data.datasource

import com.dalmuina.core.data.helpers.CrashlyticsLogger
import com.dalmuina.core.data.helpers.startOfDayMillis
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.CardHistoryEntity
import com.dalmuina.data.entity.CardProgressEntity
import com.dalmuina.data.entity.toDomain
import com.dalmuina.data.entity.toEntity
import com.dalmuina.core.data.helpers.safeDbCall
import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DailyStatsDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class RoomCardDatasource(
    private val dao: DFCardDao,
    private val logger: CrashlyticsLogger,
) : CardLocalDataSource {

    override suspend fun saveCard(card: CardDomain): DFResult<Int, DataError> =
        safeDbCall(logger) {
            dao.insert(card.toEntity()).toInt()
        }

    override suspend fun updateCard(card: CardDomain): DFResult<Int, DataError> =
        safeDbCall(logger) {
            dao.update(card.toEntity())
            card.id
        }

    override suspend fun completeCard(
        cardId: Int,
        spentMillis: Long
    ): DFResult<Int, DataError> {
        val now = System.currentTimeMillis()
        return safeDbCall(logger) {
            dao.insertProgress(CardProgressEntity(cardId = cardId))
            dao.markCompleted(cardId, now)
            dao.insertCompletedStat(
                CardHistoryEntity(
                    cardId = cardId,
                    spentMillis = spentMillis,
                    completedAt = now,
                    dayStart = now.startOfDayMillis(),
                )
            )
            cardId
        }
    }

    override suspend fun postponeCard(cardId: Int): DFResult<Int, DataError> {
        val now = System.currentTimeMillis()
        return safeDbCall(logger) {
            dao.insertProgress(CardProgressEntity(cardId = cardId))
            dao.markPostponed(cardId, now)
            cardId
        }
    }

    override fun getAllCards(): Flow<DFResult<List<CardDomain>, DataError>> =
        dao.getAllCards()
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                        as DFResult<List<CardDomain>, DataError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataError.Local.Unknown(e)))
            }

    override suspend fun getCardById(cardId: Int): DFResult<CardDomain, DataError> =
        safeDbCall(logger) {
            dao.getCardById(cardId).toDomain()
        }

    override suspend fun deleteCard(cardId: Int): EmptyResult<DataError> =
        safeDbCall(logger) {
            dao.deleteCard(cardId)
        }

    override fun getDailyStatsForCard(
        cardId: Int,
        fromDay: Long,
        toDay: Long
    ): Flow<DFResult<List<DailyStatsDomain>, DataError>> =
        dao.getDailyStatsForCard(cardId, fromDay, toDay)
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                        as DFResult<List<DailyStatsDomain>, DataError>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataError.Local.Unknown(e)))
            }
}

