package com.dalmuina.core.design_system.component.button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.core.design_system.tokens.Elevation

@Composable
fun DFButtonElevated(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    onClick: () -> Unit
) {

    ElevatedButton(
        modifier = modifier,
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = Elevation.m
        ),
        onClick = onClick,
    ) {
        text()
    }
}

