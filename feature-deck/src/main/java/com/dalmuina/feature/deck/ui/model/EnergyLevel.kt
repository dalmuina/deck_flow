package com.dalmuina.feature.deck.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dalmuina.feature.deck.R

enum class EnergyLevel {
    LOW, MEDIUM, HIGH
}

@Composable
fun EnergyLevel.toDisplayString(): String {
    return when (this) {
        EnergyLevel.LOW -> stringResource(R.string.energy_low)
        EnergyLevel.MEDIUM -> stringResource(R.string.energy_medium)
        EnergyLevel.HIGH -> stringResource(R.string.energy_high)
    }
}