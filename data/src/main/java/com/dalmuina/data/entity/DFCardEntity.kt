package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dalmuina.domain.model.DFCard

@Entity(tableName = "cards")
data class DFCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val duration: Long,
)

fun DFCard.toEntity(): DFCardEntity {
    return DFCardEntity(
        id = id,
        name = name,
        duration = durationMillis,
    )
}

