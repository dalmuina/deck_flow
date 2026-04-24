package com.dalmuina.core.design_system.component.button

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dalmuina.core.design_system.component.badge.DFLabel
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Elevation
import com.dalmuina.core.design_system.tokens.Spacing

@Composable
fun DFButtonFloating(
    modifier: Modifier = Modifier,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DFLabel(
            text = label,
        )
        Spacer(modifier = Modifier.width(Spacing.m))
        FloatingActionButton(
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = Elevation.m
            ),
            onClick = onClick
        ) {
            icon()
        }
    }
}

@DFPreview
@Composable
fun DFButtonFloatingPreview() {
    DeckFlowTheme {
        DFButtonFloating(
            label = "Create Card",
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Deck"
                )
            },
            onClick = {}
        )
    }
}