package com.dalmuina.feature.deck.ui.deckCreator

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.tokens.Spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeckCreatorRoute(
    viewModel: DeckCreatorViewModel = koinViewModel()
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    DeckCreatorScreen(
        items = todos,
    )
}

@Composable
fun DeckCreatorScreen(
    items : List<String>,
) {
    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(Spacing.l)
    ) {
        items(items, key={it}){todo->
            Text(text = todo,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                    }
                    .padding(Spacing.l))
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
fun DeckCreatorScreenPreview() {
    DeckCreatorScreen(
        items = listOf("Todo 1", "Todo 2"),
    )
}