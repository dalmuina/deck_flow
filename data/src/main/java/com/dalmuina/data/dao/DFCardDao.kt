package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dalmuina.data.entity.DFCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DFCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: DFCardEntity)

    @Update
    suspend fun update(card: DFCardEntity)

    @Query(
        """
    UPDATE cards
    SET title = :title
    WHERE id = :cardId
    """
    )
    suspend fun updateDeckName(cardId: Int, title: String)

    @Query(
        """
        SELECT * FROM cards
    """
    )
    fun getAllCards(): Flow<List<DFCardEntity>>

    @Query("""
        SELECT * FROM cards WHERE id = :idCard
    """)
    suspend fun getCardById(idCard: Int): DFCardEntity

    @Query(
        """
    DELETE FROM cards
    WHERE id = :cardId
"""
    )
    suspend fun deleteCard(cardId: Int)

}
