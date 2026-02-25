package com.dalmuina.designsystem.component.textfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens

@Composable
fun DFTimeInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) {
                    stateDescription = "$value minutes"
                },
            value = value,
            onValueChange = onValueChanged,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            label = { Text("Time") }
        )
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick =
                    onMoreTime
            ) {
                Icon(
                    modifier = Modifier
                        .size(Dimens.mediumIcons),
                    imageVector = Icons.Default.ArrowDropUp,
                    contentDescription = "More time"
                )
            }
            IconButton(
                onClick = onLessTime,
                enabled = value.toIntOrNull()?.let { it > 0 } == true
            ) {
                Icon(
                    modifier = Modifier
                        .size(Dimens.mediumIcons),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Less time"
                )
            }
        }
    }
}

@DFPreview
@Composable
fun DFTimeInputPreview() {
    DeckFlowTheme {
        DFTimeInput(
            value = "0",
            onValueChanged = {},
            onMoreTime = {},
            onLessTime = {},
        )
    }
}