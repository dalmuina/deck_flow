package com.dalmuina.core.design_system.component.badge

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Elevation
import com.dalmuina.core.design_system.tokens.Spacing

@Composable
fun DFFloatingLabel(
    modifier: Modifier = Modifier,
    text: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(25),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = Elevation.s,
        shadowElevation = Elevation.xs,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = Spacing.s,
                vertical = Spacing.xs
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@DFPreview
@Composable
fun DFFloatingLabelPreview(){
    DeckFlowTheme {
        DFFloatingLabel(
            text = "Crear Deck"
        )
    }
}