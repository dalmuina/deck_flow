package com.dalmuina.feature.deck.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckColors
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.ui.model.DFDeckUi

@Composable
fun DFDeckSlot(
    deck: DFDeckUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.deckHeight)
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            when (deck.cardCount) {
                in 0 until 1 -> MaterialTheme.colorScheme.primaryContainer
                in 2 until 3 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = deck.name,
                style = MaterialTheme.typography.titleMedium
            )

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
fun DFDeckSlotPreview() {
    DeckFlowTheme {
        DFDeckSlot(
            deck = DFDeckUi(
                id = 0,
                name = "asd",
                cardCount = 4,
            ),
            {},
            {},
        )
    }
}
