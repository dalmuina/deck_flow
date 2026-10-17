package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dalmuina.domain.model.CardDomain

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val duration: Long,
    val description: String? = null,
)

fun CardDomain.toEntity(): CardEntity =
    CardEntity(
        id = id,
        name = name,
        duration = durationMillis,
        description = description,
    )

fun CardEntity.toDomain(): CardDomain =
    CardDomain(
        id = id,
        name = name,
        durationMillis = duration,
        description = description,
    )
