package com.dalmuina.feature.deck.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dalmuina.core_ui.utils.toTimerText
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DFTheme
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.ui.model.DFCardSlotUi
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DFCardSlot(
    modifier: Modifier = Modifier,
    card: DFCardSlotUi,
    onEditCard: (Int) -> Unit,
    onCheckedChanged: (Int) -> Unit
) {
    val containerColor =
        if (card.isSelected) {
            DFTheme.extraColors.cardSlotSelectedContainer
        } else {
            DFTheme.extraColors.cardSlotUnselectedContainer
        }
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onEditCard(card.id) },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isSelected) 6.dp else 0.dp
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
                onCheckedChange = { onCheckedChanged(card.id) }
            )
        }
    }
}

@DFPreview
@Composable
fun DFCardSlotPreview() {
    DeckFlowTheme {
        DFCardSlot(
            card = DFCardSlotUi(
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
