package com.dalmuina.domain.model

data class CardDomain(
    val id: Int = 0,
    val name: String,
    val durationMillis: Long,
    val completedAt: Long? = null,
    val postponedAt: Long? = null,
    val order: Int? = null,
)
