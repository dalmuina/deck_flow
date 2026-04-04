package com.dalmuina.feature.stats.model

import androidx.compose.runtime.Immutable
import com.dalmuina.designsystem.component.select.DFSelectableOption
import com.dalmuina.domain.model.DFDeckDomain

@Immutable
data class DFDeckOptionUi(
    override val id: Int,
    override val name: String
): DFSelectableOption

fun DFDeckDomain.toUi(): DFDeckOptionUi =
    DFDeckOptionUi(
        id = id,
        name = name,
    )