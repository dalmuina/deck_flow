package com.dalmuina.data.entity

import com.dalmuina.domain.model.CardDomain

data class CardInDeckEntity(
    val id: Int,
    val name: String,
    val duration: Long,
    val completedAt: Long?,
    val postponedAt: Long?,
    val order: Int,
)

fun CardInDeckEntity.toDomain(): CardDomain =
    CardDomain(
        id = id,
        name = name,
        durationMillis = duration,
        completedAt = completedAt,
        postponedAt = postponedAt,
        order = order,
    )
