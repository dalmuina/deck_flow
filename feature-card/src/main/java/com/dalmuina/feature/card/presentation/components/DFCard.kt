package com.dalmuina.feature.card.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.card.model.DFCardUi
import com.dalmuina.feature.card.presentation.TimerState

@Composable
fun DFCard(
    card: DFCardUi,
    containerColor: Color,
) {
    DFCardContainer(containerColor = containerColor) {
        DFCardContent(card)
    }
}

@Composable
fun DFCardWithTimer(
    card: DFCardUi,
    containerColor: Color,
    timerState: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {

    DFCardContainer(containerColor = containerColor) {

        DFCardContent(card)

        Spacer(modifier = Modifier.height(Spacing.m))

        DFCountdownTimer(
            state = timerState,
            onPlay = onPlay,
            onReset = onReset,
        )
    }
}

@Composable
fun DFCardContainer(
    modifier: Modifier = Modifier,
    containerColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(Spacing.s)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun DFCardContent(card: DFCardUi) {

    Text(
        text = card.name,
        style = MaterialTheme.typography.headlineMedium
    )
}