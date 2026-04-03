package com.dalmuina.feature.card.presentation

import androidx.compose.runtime.Immutable
import com.dalmuina.feature.card.model.DFCardUi

@Immutable
data class SessionState(
    val loading: Boolean = false,
    val name: String = "",
    val cards : List<DFCardUi> = emptyList(),
    val isDeckSelected: Boolean = false,
)
