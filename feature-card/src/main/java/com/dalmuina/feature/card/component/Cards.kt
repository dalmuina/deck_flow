package com.dalmuina.feature.card.component

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
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.model.CardUi
import com.dalmuina.feature.card.presentation.timer.TimerState
import kotlin.time.Duration

@Composable
fun CardWithoutTimer(
    modifier: Modifier = Modifier,
    card: CardUi,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    CardContainer(modifier = modifier, containerColor = containerColor) {
        CardContent(card = card)
    }
}

@Composable
fun CardWithTimer(
    modifier: Modifier = Modifier,
    card: CardUi,
    containerColor: Color,
    timerState: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {

    CardContainer(modifier = modifier, containerColor = containerColor) {

        CardContent(card)

        Spacer(modifier = Modifier.height(Spacing.m))

        CountdownTimer(
            state = timerState,
            onPlay = onPlay,
            onReset = onReset,
        )
    }
}

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    containerColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = modifier,
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
fun CardContent(card: CardUi) {

    Text(
        text = card.name,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@DFPreview
@Composable
fun CardWithoutTimerPreview(){
    DeckFlowTheme {
        CardWithoutTimer(
            card = CardUi(
                id= 0,
                name = "Read",
                duration = Duration.ZERO,
                isCompleted = false,
                isPostponed = false,
            ),
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
