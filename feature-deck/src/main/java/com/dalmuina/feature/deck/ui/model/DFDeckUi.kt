package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DFDeckUi (
    val id: Int,
    val cardList: List<Int>,
)