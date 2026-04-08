package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable
import com.dalmuina.core.design_system.component.select.DFSelectableOption
import com.dalmuina.domain.model.DeckDomain

@Immutable
data class DeckOptionUi(
    override val id: Int,
    override val name: String
): DFSelectableOption

fun DeckDomain.toUi(): DeckOptionUi =
    DeckOptionUi(
        id = id,
        name = name,
    )