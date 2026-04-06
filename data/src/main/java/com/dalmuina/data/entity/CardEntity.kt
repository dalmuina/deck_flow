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
)

fun CardDomain.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        name = name,
        duration = durationMillis,
    )
}

fun CardEntity.toDomain(): CardDomain {
    return CardDomain(
        id= id,
        name = name,
        durationMillis = duration,
    )
}

