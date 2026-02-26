package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dalmuina.domain.model.DFCard

@Entity(tableName = "cards")
data class DFCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val duration: Long,
)

fun DFCard.toEntity(): DFCardEntity {
    return DFCardEntity(
        id = id,
        title = title,
        duration = durationMillis,
    )
}

fun DFCardEntity.toDomain(): DFCard {
    return DFCard (
        id = id,
        title = title,
        durationMillis = duration,
    )
}
