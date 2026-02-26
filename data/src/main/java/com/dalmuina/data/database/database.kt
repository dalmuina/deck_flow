package com.dalmuina.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.entity.DFCardEntity

@Database(
    entities = [DFCardEntity::class],
    version = 1,
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): DFCardDao
}