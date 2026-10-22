package com.dalmuina.domain.model

const val FIXED_CARD_DURATION_MILLIS = 15 * 60 * 1000L
const val MAX_CARD_DESCRIPTION_LENGTH = 300

data class CardDomain(
    val id: Int = 0,
    val name: String,
    val durationMillis: Long,
    val completedAt: Long? = null,
    val postponedAt: Long? = null,
    val order: Int? = null,
    val description: String? = null,
)
