package com.dalmuina.core.design_system.component.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme

@Composable
fun DFOutlinedTextField(
    modifier: Modifier = Modifier,
    name: String,
    onNameChanged: (String) -> Unit,
    focusRequester: FocusRequester? = null,
    label: @Composable () -> Unit,
) {
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = name,
                selection = TextRange(name.length)
            )
        )
    }

    LaunchedEffect(name) {
        if (textFieldValue.text != name) {
            textFieldValue = textFieldValue.copy(
                text = name,
                selection = TextRange(name.length)
            )
        }
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                }
            ),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onNameChanged(it.text)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor  = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Sentences,
        ),
        label = label
    )
}

@DFPreview
@Composable
fun DFOutlinedTextFieldPreview() {
    DeckFlowTheme {
        DFOutlinedTextField(
            name = "",
            focusRequester = null,
            onNameChanged = {},
            label = { Text(text = "Actividad") }
        )
    }
}