package com.dalmuina.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dalmuina.data.entity.DFCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DFCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: DFCardEntity)

    @Query("SELECT * FROM cards")
    fun getAllCards(): Flow<List<DFCardEntity>>
}
