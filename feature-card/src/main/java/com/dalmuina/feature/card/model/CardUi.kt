package com.dalmuina.feature.card.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.CardDomain
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class CardUi(
    val id: Int,
    val name: String,
    val duration: Duration,
    val isCompleted: Boolean,
    val isPostponed: Boolean,
)

fun CardDomain.toUi(): CardUi = CardUi(
    id = id,
    name = name,
    duration = durationMillis.milliseconds,
    isCompleted = completedAt != null,
    isPostponed = postponedAt != null,
)
