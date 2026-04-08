package com.dalmuina.feature.deck.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DFTheme
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Dimens
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.model.DeckUi

@Composable
fun DeckSlot(
    deck: DeckUi,
    onEdit: () -> Unit,
    onDeckSelected: (Int) -> Unit,
) {
    val containerColor =
        if (deck.isSelected) {
            DFTheme.extraColors.cardSlotSelectedContainer
        } else {
            DFTheme.extraColors.cardSlotUnselectedContainer
        }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.deckHeight),
        onClick = { onEdit() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (deck.isSelected) 6.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Checkbox(
                    checked = deck.isSelected,
                    onCheckedChange = { onDeckSelected(deck.id) }
                )
            }

            Text(
                text = stringResource(R.string.cards_count, deck.cardCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@DFPreview
@Composable
fun DeckSlotPreview() {
    DeckFlowTheme {
        DeckSlot(
            deck = DeckUi(
                id = 0,
                name = "asd",
                cardCount = 4,
                isSelected = false,
            ),
            onEdit = {},
            onDeckSelected = {},
        )
    }
}
