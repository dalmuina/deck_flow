package com.dalmuina.feature.card.ui

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.card.R
import com.dalmuina.feature.card.model.DFCardUi
import com.dalmuina.feature.card.ui.component.DFCard
import com.dalmuina.feature.card.ui.component.DFSwipeCard
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardRoute(
    viewModel: CardViewModel = koinViewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CardScreen(
        name = state.name,
        cards = state.cards.take(3),
        onSwiped = { viewModel.process(CardIntent.CompleteTopCard) }
    )
}

@Composable
fun CardScreen(
    name: String,
    cards: List<DFCardUi>,
    onSwiped: () -> Unit
) {
    val isEmpty = cards.isEmpty()

    if (isEmpty) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_deck_created))
        }
    } else {
        Box(
            modifier = Modifier
                .padding(Spacing.l)
                .fillMaxSize()
        ) {

            cards.reversed().forEachIndexed { index, card ->

                val depth = cards.size - index - 1

                val targetScale = 1f - (depth * 0.05f)

                val scale by animateFloatAsState(
                    targetValue = targetScale,
                    label = "cardScale"
                )

                val targetOffset = (depth * 16).dp

                val offset by animateDpAsState(
                    targetValue = targetOffset,
                    label = "cardOffset"
                )

                val elevation = (8 - depth * 2).dp

                val modifier = Modifier
                    .fillMaxSize()
                    .offset(y = targetOffset)
                    .scale(scale)

                if (depth == 0) {
                    DFSwipeCard(
                        modifier = modifier,
                        onSwiped = onSwiped
                    ) {
                        DFCard(card = card)
                    }
                } else {
                    Card(
                        modifier = modifier,
                        elevation = CardDefaults.cardElevation(elevation)
                    ) {
                        DFCard(card = card)
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_7
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL_7
)
@Composable
fun CardScreenPreview() {
    CardScreen(
        name = "My Deck",
        cards = listOf(
            DFCardUi(
                id = 0,
                name = "leer",
                duration = 3L.minutes
            ),
            DFCardUi(
                id = 1,
                name = "escribir",
                duration = 30L.minutes
            ),
            DFCardUi(
                id = 2,
                name = "estudiar",
                duration = 1L.hours
            )
        ),
        onSwiped = {}
    )
}
