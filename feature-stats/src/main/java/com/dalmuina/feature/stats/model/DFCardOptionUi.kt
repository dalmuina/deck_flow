package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable
import com.dalmuina.designsystem.component.select.DFSelectableOption
import com.dalmuina.domain.model.DFCardDomain

@Immutable
data class DFCardOptionUi(
    override val id: Int,
    override val name: String
): DFSelectableOption

fun DFCardDomain.toDFCardOptionUi(): DFCardOptionUi =
    DFCardOptionUi(
        id = id,
        name = name,
    )
