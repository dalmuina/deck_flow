package com.dalmuina.feature.card.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DFCardDomain
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class DFCardUi(
    val id: Int,
    val name: String,
    val duration: Duration,
    val isCompleted: Boolean,
    val isPostponed: Boolean,
)

fun DFCardDomain.toUi(): DFCardUi = DFCardUi(
    id = id,
    name = name,
    duration = durationMillis.milliseconds,
    isCompleted = completedAt != null,
    isPostponed = postponedAt != null,
)
