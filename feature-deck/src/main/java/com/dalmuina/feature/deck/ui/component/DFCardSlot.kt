package com.dalmuina.feature.deck.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.ui.model.CardUi
import com.dalmuina.feature.deck.ui.model.toTimerText
import java.util.UUID
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DFCardSlot(
    modifier: Modifier = Modifier,
    card: CardUi,
    onCheckedChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
        ){
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(Spacing.l)
        ) {
            Text(
                text = card.title
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = card.duration.toTimerText()
            )
        }
        Checkbox(
            modifier = Modifier
                .padding(Spacing.l),
            checked = card.isChecked,
            onCheckedChange = {onCheckedChanged(card.id)}
        )
    }
}
}

@DFPreview
@Composable
fun DFCardSlotPreview() {
    DeckFlowTheme {
        DFCardSlot(
            card=CardUi(
                id = UUID.randomUUID().toString(),
                title = "Test",
                duration = 0L.hours + 3L.minutes + 25L.seconds,
                false,
            ),
            onCheckedChanged = {},
        )
    }
}