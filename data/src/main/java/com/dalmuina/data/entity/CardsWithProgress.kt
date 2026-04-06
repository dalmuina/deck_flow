package com.dalmuina.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.dalmuina.domain.model.CardDomain

data class CardWithProgress(

    @Embedded
    val card: CardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val progress: CardProgressEntity?
)

fun CardWithProgress.toDomain(): CardDomain {
    return CardDomain(
        id = card.id,
        name = card.name,
        durationMillis = card.duration,
        completedAt = progress?.completedAt,
        postponedAt = progress?.postponeAt,
    )
}