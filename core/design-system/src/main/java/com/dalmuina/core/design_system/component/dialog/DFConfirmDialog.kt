package com.dalmuina.core.design_system.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.dalmuina.core.design_system.R
import com.dalmuina.core.design_system.component.button.DFButton
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing

@Composable
fun DFConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Corner.m)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.l)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.padding(top = Spacing.m))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.padding(top = Spacing.xl))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    DFButton(
                        text = { Text(text = stringResource(R.string.cancel_button)) },
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(Spacing.l))
                    DFButton(
                        text = { Text(text = stringResource(R.string.delete_button)) },
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

@DFPreview
@Composable
fun DFConfirmDialogPreview() {
    DeckFlowTheme {
        DFConfirmDialog(
            title = "Delete Card",
            message = "Are you sure you want to delete Fitness?",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
