package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Immutable
import com.dalmuina.domain.model.DFDeckDomain

@Immutable
data class DFDeckUi (
    val id: Int,
    val name: String,
    val cardCount: Int,
    val isSelected: Boolean,
)

fun DFDeckDomain.toDeckUi(): DFDeckUi {
    return DFDeckUi(
        id = id,
        name = name,
        cardCount = cards.size,
        isSelected = false,
    )
}
