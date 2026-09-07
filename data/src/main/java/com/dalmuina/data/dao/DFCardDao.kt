package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dalmuina.data.entity.CardEntity
import com.dalmuina.data.entity.CardHistoryEntity
import com.dalmuina.data.entity.CardProgressEntity
import com.dalmuina.data.entity.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DFCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(progress: CardProgressEntity)

    @Query(
        """
    UPDATE card_progress
    SET completedAt = :time
    WHERE cardId = :cardId
    """,
    )
    suspend fun markCompleted(
        cardId: Int,
        time: Long,
    )

    @Query(
        """
    UPDATE card_progress
    SET postponeAt = :time
    WHERE cardId = :cardId
    """,
    )
    suspend fun markPostponed(
        cardId: Int,
        time: Long,
    )

    @Insert
    suspend fun insertCompletedStat(stat: CardHistoryEntity): Long

    @Transaction
    @Query(
        """
        SELECT * FROM cards
    """,
    )
    fun getAllCards(): Flow<List<CardEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM cards WHERE id = :idCard
    """,
    )
    suspend fun getCardById(idCard: Int): CardEntity

    @Query(
        """
    DELETE FROM cards
    WHERE id = :cardId
""",
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
    """,
    )
    fun getDailyStatsForDeck(
        deckId: Int,
        fromDay: Long,
        toDay: Long,
    ): Flow<List<DailyStatsEntity>>

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
    """,
    )
    fun getDailyStatsForCard(
        cardId: Int,
        fromDay: Long,
        toDay: Long,
    ): Flow<List<DailyStatsEntity>>
}
