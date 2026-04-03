package com.dalmuina.data.datasource

import com.dalmuina.core.helpers.startOfDayMillis
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.DFCardEntity
import com.dalmuina.data.entity.DFCardHistoryEntity
import com.dalmuina.data.entity.DFCardProgressEntity
import com.dalmuina.data.entity.DFDailyStatsEntity
import kotlinx.coroutines.flow.Flow

class LocalCardDataSource(
    private val dao: DFCardDao
) {
    suspend fun saveCard(card: DFCardEntity) =
        dao.insert(card)

    suspend fun updateCard(card: DFCardEntity) =
        dao.update(card)

    suspend fun completeCard(cardId: Int, completedAt: Long, spentMillis: Long) {
        dao.insertProgress(
            DFCardProgressEntity(cardId = cardId)
        )
        dao.markCompleted(cardId, completedAt)

        dao.insertCompletedStat(
            DFCardHistoryEntity(
                cardId = cardId,
                spentMillis = spentMillis,
                completedAt = completedAt,
                dayStart = completedAt.startOfDayMillis()
            )
        )
    }

    suspend fun postponeCard(cardId: Int, time: Long) {
        dao.insertProgress(
            DFCardProgressEntity(cardId = cardId)
        )
        dao.markPostponed(cardId, time)
    }

    fun getAllCards(): Flow<List<DFCardEntity>> =
        dao.getAllCards()

    suspend fun getCardByID(cardId: Int): DFCardEntity =
        dao.getCardById(cardId)

    suspend fun deleteCard(cardId: Int) =
        dao.deleteCard(cardId)

    fun getDailyStatsForCard(cardId: Int, fromDay: Long, toDay: Long): Flow<List<DFDailyStatsEntity>> =
        dao.getDailyStatsForCard(cardId, fromDay, toDay)
}
