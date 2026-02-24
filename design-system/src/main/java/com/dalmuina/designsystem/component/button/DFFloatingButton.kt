package com.dalmuina.designsystem.component.button

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.designsystem.tokens.Dimens

@Composable
fun DFFloatingButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = Dimens.defaultElevation
        ),
        onClick = onClick
    ) {
        icon()
    }
}