package com.dalmuina.feature.card.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing

private val DESCRIPTION_MAX_HEIGHT = 240.dp

@Composable
fun CardDescriptionDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Corner.m),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(Spacing.l),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.padding(top = Spacing.m))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = DESCRIPTION_MAX_HEIGHT)
                            .verticalScroll(rememberScrollState()),
                )
            }
        }
    }
}

@DFPreview
@Composable
fun CardDescriptionDialogPreview() {
    DeckFlowTheme {
        CardDescriptionDialog(
            title = "Reading",
            description = "Read chapter 3 of the book and take notes on the key ideas.",
            onDismiss = {},
        )
    }
}

@DFPreview
@Composable
fun CardDescriptionDialogLongTextPreview() {
    DeckFlowTheme {
        CardDescriptionDialog(
            title = "Reading with a very long activity name that wraps onto two lines",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(12),
            onDismiss = {},
        )
    }
}
