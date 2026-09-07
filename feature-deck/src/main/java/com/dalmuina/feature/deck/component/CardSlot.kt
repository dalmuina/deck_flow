package com.dalmuina.feature.deck.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.component.checkbox.DFCheckBox
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
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
    dragHandleModifier: Modifier = Modifier,
    onEditCard: (Int) -> Unit,
    onCheckedChanged: (Int) -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val shape = RoundedCornerShape(Corner.xs)

    if (card.isSelected) {
        val brush = Brush.linearGradient(listOf(primary, secondary))
        Card(
            modifier =
                modifier
                    .fillMaxWidth(),
            onClick = { onEditCard(card.id) },
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.m),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(brush),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = dragHandleModifier.padding(start = Spacing.m),
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                )
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(Spacing.l),
                ) {
                    Text(
                        text = "${card.order ?: ""} ${card.name}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(Spacing.s))
                    Text(
                        text = card.duration.toTimerText(),
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                DFCheckBox(
                    modifier = Modifier.padding(Spacing.l),
                    checked = card.isSelected,
                ) { onCheckedChanged(card.id) }
            }
        }
    } else {
        Card(
            modifier =
                modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = shape),
            onClick = { onEditCard(card.id) },
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(Spacing.l),
                ) {
                    Text(
                        text = "${card.order ?: ""} ${card.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(Spacing.s))
                    Text(
                        text = card.duration.toTimerText(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                DFCheckBox(
                    modifier = Modifier.padding(Spacing.l),
                    checked = card.isSelected,
                ) { onCheckedChanged(card.id) }
            }
        }
    }
}

@DFPreview
@Composable
fun CardSlotActivePreview() {
    DeckFlowTheme {
        CardSlot(
            card = CardUi(id = 0, name = "Test", duration = 0L.hours + 3L.minutes + 25L.seconds, true),
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
            card = CardUi(id = 0, name = "Test", duration = 0L.hours + 3L.minutes + 25L.seconds, false),
            onEditCard = {},
            onCheckedChanged = {},
        )
    }
}
