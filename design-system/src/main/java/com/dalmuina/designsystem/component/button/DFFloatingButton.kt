package com.dalmuina.designsystem.component.button

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dalmuina.designsystem.component.badge.DFFloatingLabel
import com.dalmuina.designsystem.component.badge.DFSwipeBadge
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing

@Composable
fun DFFloatingButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ){
        DFFloatingLabel(
            text = label,
        )
        Spacer(modifier = Modifier.width(Spacing.m))
        FloatingActionButton(
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = Dimens.defaultElevation
            ),
            onClick = onClick
        ) {
            icon()
        }
    }
}

@DFPreview
@Composable
fun DFFloatingButtonPreview() {
    DeckFlowTheme {
        DFFloatingButton(
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