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
    title: String,
    label: @Composable () -> Unit,
    onTitleChanged: (String)->Unit,
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = title,
        onValueChange = onTitleChanged,
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
            title = "",
            label = {Text(text= "Actividad")}
        ) { }
    }
}