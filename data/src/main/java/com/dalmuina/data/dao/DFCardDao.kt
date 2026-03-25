package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dalmuina.data.entity.DFCardEntity
import com.dalmuina.data.entity.DFCardHistoryEntity
import com.dalmuina.data.entity.DFCardProgressEntity
import com.dalmuina.data.entity.DFCardWithProgress
import com.dalmuina.data.entity.DFDailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DFCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: DFCardEntity): Long

    @Update
    suspend fun update(card: DFCardEntity): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(progress: DFCardProgressEntity)

    @Query(
        """
    UPDATE card_progress
    SET completedAt = :time
    WHERE cardId = :cardId
    """
    )
    suspend fun markCompleted(cardId: Int, time: Long)

    @Query(
        """
    UPDATE card_progress
    SET postponeAt = :time
    WHERE cardId = :cardId
    """
    )
    suspend fun markPostponed(cardId: Int, time: Long)

    @Insert
    suspend fun insertCompletedStat(stat: DFCardHistoryEntity): Long

    @Transaction
    @Query(
        """
        SELECT * FROM cards
    """
    )
    fun getAllCards(): Flow<List<DFCardEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM cards WHERE id = :idCard
    """
    )
    suspend fun getCardById(idCard: Int): DFCardEntity

    @Query(
        """
    DELETE FROM cards
    WHERE id = :cardId
"""
    )
    suspend fun deleteCard(cardId: Int)

    @Query(
        """
    SELECT
        h.dayStart AS dayStart,
        SUM(h.spentMillis) AS totalSpentMillis,
        COUNT(h.id) AS completedCount
    FROM card_history h
    INNER JOIN deck_card_cross_ref x ON x.cardId = h.cardId
    WHERE x.deckId = :deckId
      AND h.dayStart BETWEEN :fromDay AND :toDay
    GROUP BY h.dayStart
    ORDER BY h.dayStart ASC
    """
    )
    suspend fun getDailyStatsForDeck(
        deckId: Int,
        fromDay: Long,
        toDay: Long
    ): List<DFDailyStatsEntity>

    @Query(
        """
    SELECT
        h.dayStart AS dayStart,
        SUM(h.spentMillis) AS totalSpentMillis,
        COUNT(h.id) AS completedCount
    FROM card_history h
    WHERE h.cardId = :cardId
      AND h.dayStart BETWEEN :fromDay AND :toDay
    GROUP BY h.dayStart
    ORDER BY h.dayStart ASC
    """
    )
    suspend fun getDailyStatsForCard(
        cardId: Int,
        fromDay: Long,
        toDay: Long
    ): List<DFDailyStatsEntity>

}
