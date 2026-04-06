package com.dalmuina.feature.deck.presentation.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.CardDomain
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class CardSlotUi(
    val id: Int=0,
    val name: String,
    val duration: Duration,
    val isSelected: Boolean,
    val order: Int? = null,
)

fun CardDomain.toUi(): CardSlotUi = CardSlotUi(
    id = id,
    name = name,
    duration = durationMillis.milliseconds,
    isSelected = true,
    order = order,
)
