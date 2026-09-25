package com.dalmuina.core.design_system.component.button

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.core.design_system.component.infoState.DFButtonLoading
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme

@Composable
fun DFButton(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    isLoading: Boolean = false,
    isEnable: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = isEnable,
        onClick = onClick,
    ) {
        AnimatedContent(targetState = isLoading) { isLoading ->
            if (isLoading) {
                DFButtonLoading()
            } else {
                text()
            }
        }
    }
}

@DFPreview
@Composable
fun DFButtonPreview() {
    DeckFlowTheme {
        DFButton(
            text = { Text(text = "Create") },
        ) { }
    }
}
