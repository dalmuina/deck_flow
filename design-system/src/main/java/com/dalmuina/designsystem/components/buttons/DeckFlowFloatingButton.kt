package com.dalmuina.designsystem.components.buttons

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.dalmuina.designsystem.tokens.Dimens

@Composable
fun DeckFlowFloatingButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = Dimens.defaultElevation
        ),
        onClick = onClick) {
        Icon(icon, contentDescription)
    }
}