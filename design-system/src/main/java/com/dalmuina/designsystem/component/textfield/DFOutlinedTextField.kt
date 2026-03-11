package com.dalmuina.designsystem.component.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme

@Composable
fun DFOutlinedTextField(
    modifier: Modifier = Modifier,
    name: String,
    label: @Composable () -> Unit,
    onNameChanged: (String)->Unit,
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = name,
        onValueChange = onNameChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        label = {label()}
    )
}

@DFPreview
@Composable
fun DFOutlinedTextFieldPreview() {
    DeckFlowTheme() {
        DFOutlinedTextField(
            name = "",
            label = {Text(text= "Actividad")}
        ) { }
    }
}