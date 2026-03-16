package com.dalmuina.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.dalmuina.domain.model.DFCard

data class DFCardWithProgress(

    @Embedded
    val card: DFCardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val progress: DFCardProgressEntity?
)

fun DFCardWithProgress.toDomain(): DFCard {
    return DFCard(
        id = card.id,
        name = card.name,
        durationMillis = card.duration,
        completedAt = progress?.completedAt,
        postponedAt = progress?.postponeAt
    )
}