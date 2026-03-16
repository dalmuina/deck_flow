package com.dalmuina.feature.card.ui

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.component.badge.DFSwipeBadge
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.theme.GreenPop
import com.dalmuina.designsystem.theme.OrangePop
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.card.R
import com.dalmuina.feature.card.model.DFCardUi
import com.dalmuina.feature.card.model.SwipeDirection
import com.dalmuina.feature.card.ui.component.DFCard
import com.dalmuina.feature.card.ui.component.DFSwipeCard
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardRoute(
    viewModel: CardViewModel = koinViewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CardScreen(
        deckName = state.name,
        cards = state.cards.take(3),
        onSwiped = { direction -> viewModel.process(CardIntent.SwipeTopCard(direction)) })
}

@Composable
fun CardScreen(
    deckName: String, cards: List<DFCardUi>, onSwiped: (SwipeDirection) -> Unit
) {
    val isEmpty = cards.isEmpty()

    if (isEmpty) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_deck_created))
        }
    } else {
        Box(
            modifier = Modifier
                .padding(Spacing.l)
                .fillMaxSize()
        ) {
            var dragOffsetX by remember { mutableFloatStateOf(0f) }
            val swipeProgress = (abs(dragOffsetX) / 300f).coerceIn(0f, 1f)
            val signedProgress = (dragOffsetX / 300f).coerceIn(-1f, 1f)

            cards.reversed().forEachIndexed { index, card ->
                val depth = cards.size - index - 1
                val baseScale = 1f - (depth * 0.05f)

                val targetScale =
                    if (depth == 1) {
                        baseScale + (0.05f * swipeProgress)
                    } else {
                        baseScale
                    }

                val scale by animateFloatAsState(
                    targetValue = targetScale, label = "cardScale"
                )

                val baseOffset = (depth * 16).dp

                val targetOffset =
                    if (depth == 1) {
                        baseOffset * (1 - swipeProgress)
                    } else {
                        baseOffset
                    }

                val offset by animateDpAsState(
                    targetValue = targetOffset, label = "cardOffset"
                )

                if (depth == 0) {
                    DFSwipeCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = offset)
                            .scale(scale),
                        onSwiped = { direction ->
                            dragOffsetX = 0f
                            onSwiped(direction)
                        },
                        onDragProgress = { dragOffsetX = it },
                    ) {
                        SwipeCardContent(signedProgress = signedProgress, card = card)
                    }
                } else {
                    DFCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = offset)
                            .scale(scale),
                        card = card
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeCardContent(
    modifier: Modifier = Modifier,
    signedProgress: Float,
    card: DFCardUi
) {

    val leftAlphaAnimated = (-signedProgress).coerceIn(0f, 1f)
    val rightAlphaAnimated = (signedProgress).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        DFCard(card = card)
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.l),
            contentAlignment = Alignment.Center){
            DFSwipeBadge(
                text = when{
                    card.isCompleted -> "Completed"
                    card.isPostponed -> "Postponed"
                    else -> ""
                },
                color = MaterialTheme.colorScheme.onBackground,
                alpha = if (card.isCompleted || card.isPostponed) 1f else 0f
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Spacing.l),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            DFSwipeBadge(
                text = "Completed",
                color = GreenPop,
                alpha = rightAlphaAnimated
            )
            DFSwipeBadge(
                text = "Postponed",
                color = OrangePop,
                alpha = leftAlphaAnimated
            )
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
    DeckFlowTheme {
        CardScreen(
            deckName = "My Deck", cards = listOf(
                DFCardUi(
                    id = 0,
                    name = "leer",
                    duration = 3L.minutes,
                    isCompleted = true,
                    isPostponed = false,
                ), DFCardUi(
                    id = 1,
                    name = "escribir",
                    duration = 30L.minutes,
                    isCompleted = false,
                    isPostponed = false,
                ), DFCardUi(
                    id = 2,
                    name = "estudiar",
                    duration = 1L.hours,
                    isCompleted = false,
                    isPostponed = true,
                )
            ), onSwiped = {})
    }
}
