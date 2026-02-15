package com.dalmuina.feature.deck.ui.deckCreator

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.components.buttons.DeckFlowFloatingButton
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeckCreatorRoute(
    viewModel: DeckCreatorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DeckCreatorScreen(
        items = state.deckCard,
        onCreate = { viewModel.process(DeckCreatorIntent.Create) }
    )
}

@Composable
fun DeckCreatorScreen(
    items: List<String>,
    onCreate: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(
                start = Spacing.l,
                top = Spacing.l,
                end = Spacing.l,
                bottom = Dimens.fabSpacing
            )
        ) {
            items(items) { todo ->
                Text(
                    text = todo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                        }
                        .padding(Spacing.l))
            }
        }
        DeckFlowFloatingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.l),
            icon = Icons.Default.Add,
            contentDescription = "Create Deck",
            onClick = onCreate
        )
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
fun DeckCreatorScreenPreview() {
    DeckCreatorScreen(
        items = listOf("Todo 1", "Todo 2"),
        {}
    )
}