package com.dalmuina.feature.deck.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Elevation
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.core.presentation.helpers.toTimerText
import com.dalmuina.feature.deck.model.CardUi
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun CardSlot(
    modifier: Modifier = Modifier,
    card: CardUi,
    onEditCard: (Int) -> Unit,
    onCheckedChanged: (Int) -> Unit
) {
    val containerColor =
        if (card.isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        }
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onEditCard(card.id) },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isSelected) Elevation.m else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Spacing.l)
            ) {
                Text(
                    text = "${card.order ?: ""} ${card.name}"
                )
                Spacer(modifier = Modifier.height(Spacing.s))
                Text(
                    text = card.duration.toTimerText()
                )
            }
            Checkbox(
                modifier = Modifier
                    .padding(Spacing.l),
                checked = card.isSelected,
                colors =  CheckboxDefaults.colors (
                    checkedColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                    checkmarkColor = MaterialTheme.colorScheme.secondary,
                    disabledCheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                    disabledUncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                    disabledIndeterminateColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                ),
                onCheckedChange = { onCheckedChanged(card.id) }
            )
        }
    }
}

@DFPreview
@Composable
fun CardSlotActivePreview() {
    DeckFlowTheme {
        CardSlot(
            card = CardUi(
                id = 0,
                name = "Test",
                duration = 0L.hours + 3L.minutes + 25L.seconds,
                true,
            ),
            onEditCard = {},
            onCheckedChanged = {},
        )
    }
}

@DFPreview
@Composable
fun CardSlotNoActivePreview() {
    DeckFlowTheme {
        CardSlot(
            card = CardUi(
                id = 0,
                name = "Test",
                duration = 0L.hours + 3L.minutes + 25L.seconds,
                false,
            ),
            onEditCard = {},
            onCheckedChanged = {},
        )
    }
}
