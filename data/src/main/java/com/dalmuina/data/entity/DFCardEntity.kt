package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dalmuina.domain.model.DFCardDomain

@Entity(tableName = "cards")
data class DFCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val duration: Long,
)

fun DFCardDomain.toEntity(): DFCardEntity {
    return DFCardEntity(
        id = id,
        name = name,
        duration = durationMillis,
    )
}

fun DFCardEntity.toDomain(): DFCardDomain {
    return DFCardDomain(
        id= id,
        name = name,
        durationMillis = duration,
    )
}

