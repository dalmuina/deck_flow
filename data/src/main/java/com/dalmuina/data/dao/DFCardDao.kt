package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dalmuina.data.entity.DFCardEntity
import com.dalmuina.data.entity.DFCardProgressEntity
import com.dalmuina.data.entity.DFCardWithProgress
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

}
