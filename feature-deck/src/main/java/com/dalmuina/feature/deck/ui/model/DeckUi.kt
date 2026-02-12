package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeckUi (
    val id: Int,
    val cardList: List<Int>,
    val energy: EnergyLevel,
)