package com.dalmuina.feature.card.ui.component

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
import androidx.compose.ui.unit.dp
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.card.model.DFCardUi
import com.dalmuina.feature.card.ui.TimerState

@Composable
fun DFCard(
    card: DFCardUi
) {
    DFCardContainer(card = card) {
        DFCardContent(card)
    }
}

@Composable
fun DFCardWithTimer(
    card: DFCardUi,
    timerState: TimerState,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {

    DFCardContainer(card = card) {

        DFCardContent(card)

        Spacer(modifier = Modifier.height(Spacing.m))

        DFCountdownTimer(
            state = timerState,
            onStart = onStart,
            onStop = onStop
        )
    }
}

@Composable
fun DFCardContainer(
    modifier: Modifier = Modifier,
    card: DFCardUi,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = arrayOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
    )

    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = colors[card.id % colors.size]
        ),
        elevation = CardDefaults.cardElevation(8.dp)
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