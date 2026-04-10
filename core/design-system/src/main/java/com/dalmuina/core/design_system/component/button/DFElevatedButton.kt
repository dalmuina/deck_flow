package com.dalmuina.core.design_system.component.button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dalmuina.core.design_system.tokens.Dimens

@Composable
fun DFElevatedButton(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    onClick: () -> Unit
) {

    ElevatedButton(
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = Dimens.mediumElevation
        ),
        onClick = onClick,
    ) {
        text()
    }
}

