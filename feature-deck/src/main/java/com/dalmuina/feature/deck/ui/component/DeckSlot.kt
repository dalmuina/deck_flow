package com.dalmuina.feature.deck.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckColors
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.ui.model.DeckUi
import com.dalmuina.feature.deck.ui.model.EnergyLevel
import com.dalmuina.feature.deck.ui.model.toDisplayString

@Composable
fun DFDeckSlot(
    deck: DeckUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.deckHeight)
            .clickable {
                onEdit()
            },
        colors = CardDefaults.cardColors(
            when (deck.energy) {
                EnergyLevel.LOW -> DeckColors.Low
                EnergyLevel.MEDIUM -> DeckColors.Medium
                EnergyLevel.HIGH -> DeckColors.High
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.l),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(
                    R.string.energy_deck,
                    deck.energy.toDisplayString()
                )
            )

            Text(
                stringResource(
                    R.string.cards_count,
                    deck.cardList.size
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                Text(
                    text = "Edit",
                    modifier = Modifier.clickable { onEdit() }
                )
                Text(
                    text = "Delete",
                    modifier = Modifier.clickable { onDelete() }
                )
            }

        }
    }
}

@DFPreview
@Composable
fun DFDeckSlotPreview() {
    DeckFlowTheme {
        DFDeckSlot(
            deck = DeckUi(
                id = 0,
                cardList = listOf(1),
                energy = EnergyLevel.MEDIUM
            ),
            {},
            {},
        )
    }
}
