package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable
import com.dalmuina.core.design_system.component.select.DFSelectableOption
import com.dalmuina.domain.model.CardDomain

@Immutable
data class CardOptionUi(
    override val id: Int,
    override val name: String,
) : DFSelectableOption

fun CardDomain.toUi(): CardOptionUi =
    CardOptionUi(
        id = id,
        name = name,
    )
