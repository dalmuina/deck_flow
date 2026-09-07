package com.dalmuina.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dalmuina.data.dao.DFCardDao
import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.CardEntity
import com.dalmuina.data.entity.CardHistoryEntity
import com.dalmuina.data.entity.CardProgressEntity
import com.dalmuina.data.entity.DeckCardCrossEntity
import com.dalmuina.data.entity.DeckEntity

@Database(
    entities = [CardEntity::class, DeckEntity::class, DeckCardCrossEntity::class, CardProgressEntity::class, CardHistoryEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): DFCardDao

    abstract fun deckDao(): DFDeckDao
}
