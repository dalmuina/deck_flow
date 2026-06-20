package com.dalmuina.feature.deck.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.component.checkbox.DFCheckBox
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.model.DeckUi

@Composable
fun DeckSlot(
    deck: DeckUi,
    onEdit: () -> Unit,
    onDeckSelected: (Int) -> Unit,
) {
    val height by animateDpAsState(
        targetValue = if (deck.isSelected) 160.dp else 100.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "deck_height_${deck.id}"
    )

    if (deck.isSelected) {
        HeroDeckSlot(
            deck = deck,
            height = height,
            onEdit = onEdit,
            onDeckSelected = onDeckSelected
        )
    } else {
        RegularDeckSlot(
            deck = deck,
            height = height,
            onEdit = onEdit,
            onDeckSelected = onDeckSelected
        )
    }
}

@Composable
private fun HeroDeckSlot(
    deck: DeckUi,
    height: Dp,
    onEdit: () -> Unit,
    onDeckSelected: (Int) -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val brush = Brush.linearGradient(listOf(primary, secondary))
    val shape = RoundedCornerShape(Corner.s)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        onClick = onEdit,
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush),
        ) {
            // Decorative circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.l),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = deck.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                    Checkbox(
                        checked = deck.isSelected,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.White.copy(alpha = 0.7f),
                            uncheckedColor = Color.White.copy(alpha = 0.7f),
                            checkmarkColor = Color.White,
                        ),
                        onCheckedChange = { onDeckSelected(deck.id) },
                    )
                }

                Text(
                    text = stringResource(R.string.cards_count, deck.cardCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun RegularDeckSlot(
    deck: DeckUi,
    height: Dp,
    onEdit: () -> Unit,
    onDeckSelected: (Int) -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val shape = RoundedCornerShape(Corner.s)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .border(width = 1.dp, color = borderColor, shape = shape),
        onClick = onEdit,
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                DFCheckBox(
                    modifier = Modifier.padding(Spacing.s),
                    checked = deck.isSelected,
                ) { onDeckSelected(deck.id) }
            }

            Text(
                text = stringResource(R.string.cards_count, deck.cardCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@DFPreview
@Composable
fun DeckSlotNotSelectedPreview() {
    DeckFlowTheme {
        DeckSlot(
            deck = DeckUi(id = 0, name = "Rutina", cardCount = 4, isSelected = false),
            onEdit = {},
            onDeckSelected = {},
        )
    }
}

@DFPreview
@Composable
fun DeckSlotSelectedPreview() {
    DeckFlowTheme {
        DeckSlot(
            deck = DeckUi(id = 0, name = "Rutina", cardCount = 4, isSelected = true),
            onEdit = {},
            onDeckSelected = {},
        )
    }
}
