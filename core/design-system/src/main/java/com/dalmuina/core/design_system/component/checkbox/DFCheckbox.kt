package com.dalmuina.core.design_system.component.checkbox

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DFCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onSelected: () -> Unit,
) {
    Checkbox(
        modifier = modifier,
        checked = checked,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.onSecondary,
            uncheckedColor = MaterialTheme.colorScheme.outline,
            checkmarkColor = MaterialTheme.colorScheme.secondary,
            disabledCheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
            disabledUncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
            disabledIndeterminateColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
        ),
        onCheckedChange = { onSelected() }
    )
}
