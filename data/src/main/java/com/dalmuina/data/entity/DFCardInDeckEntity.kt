package com.dalmuina.data.entity

import com.dalmuina.domain.model.DFCardDomain

data class DFCardInDeckEntity(
    val id: Int,
    val name: String,
    val duration: Long,
    val completedAt: Long?,
    val postponedAt: Long?,
    val order: Int
)

fun DFCardInDeckEntity.toDomain(): DFCardDomain {
    return DFCardDomain(
        id = id,
        name = name,
        durationMillis = duration,
        completedAt = completedAt,
        postponedAt = postponedAt,
        order = order,
    )
}