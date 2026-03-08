package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="decks")
data class DFDeckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)
