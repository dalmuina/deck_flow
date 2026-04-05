package com.dalmuina.data.datasource

import com.dalmuina.core.helpers.startOfDayMillis
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.DFCardHistoryEntity
import com.dalmuina.data.entity.DFCardProgressEntity
import com.dalmuina.data.entity.toDomain
import com.dalmuina.data.entity.toEntity
import com.dalmuina.data.helpers.safeDbCall
import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFDailyStatsDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class RoomCardDatasource(
    private val dao: DFCardDao,
) : CardLocalDataSource {

    override suspend fun saveCard(card: DFCardDomain): DFResult<Int, DataError.Local> =
        safeDbCall {
            dao.insert(card.toEntity()).toInt()
        }

    override suspend fun updateCard(card: DFCardDomain): DFResult<Int, DataError.Local> =
        safeDbCall {
            dao.update(card.toEntity())
            card.id
        }

    override suspend fun completeCard(
        cardId: Int,
        spentMillis: Long
    ): DFResult<Int, DataError.Local> {
        val now = System.currentTimeMillis()
        return safeDbCall {
            dao.insertProgress(DFCardProgressEntity(cardId = cardId))
            dao.markCompleted(cardId, now)
            dao.insertCompletedStat(
                DFCardHistoryEntity(
                    cardId = cardId,
                    spentMillis = spentMillis,
                    completedAt = now,
                    dayStart = now.startOfDayMillis(),
                )
            )
            cardId
        }
    }

    override suspend fun postponeCard(cardId: Int): DFResult<Int, DataError.Local> {
        val now = System.currentTimeMillis()
        return safeDbCall {
            dao.insertProgress(DFCardProgressEntity(cardId = cardId))
            dao.markPostponed(cardId, now)
            cardId
        }
    }

    override fun getAllCards(): Flow<DFResult<List<DFCardDomain>, DataError.Local>> =
        dao.getAllCards()
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                        as DFResult<List<DFCardDomain>, DataError.Local>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataError.Local.Unknown(e)))
            }

    override suspend fun getCardById(cardId: Int): DFResult<DFCardDomain, DataError.Local> =
        safeDbCall {
            dao.getCardById(cardId).toDomain()
        }

    override suspend fun deleteCard(cardId: Int): EmptyResult<DataError.Local> =
        safeDbCall {
            dao.deleteCard(cardId)
        }

    override fun getDailyStatsForCard(
        cardId: Int,
        fromDay: Long,
        toDay: Long
    ): Flow<DFResult<List<DFDailyStatsDomain>, DataError.Local>> =
        dao.getDailyStatsForCard(cardId, fromDay, toDay)
            .map { entities ->
                DFResult.Success(entities.map { it.toDomain() })
                        as DFResult<List<DFDailyStatsDomain>, DataError.Local>
            }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(DFResult.Error(DataError.Local.Unknown(e)))
            }
}

