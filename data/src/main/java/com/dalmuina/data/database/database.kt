package com.dalmuina.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.DFCardEntity
import com.dalmuina.data.entity.DFDeckCardCrossRef
import com.dalmuina.data.entity.DFDeckEntity

@Database(
    entities = [DFCardEntity::class, DFDeckEntity::class, DFDeckCardCrossRef::class],
    version = 1,
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): DFCardDao
    abstract fun deckDao(): DFDeckDao
}