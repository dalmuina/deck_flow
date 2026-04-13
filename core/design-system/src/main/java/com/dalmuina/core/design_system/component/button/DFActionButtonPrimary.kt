package com.dalmuina.core.design_system.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Dimen
import com.dalmuina.core.design_system.tokens.IconSize

@Composable
fun DFActionButtonPrimary(
    modifier : Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onAction: ()->Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(Dimen.l)
            .clip(RoundedCornerShape(Corner.s))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)),
                onClick = {onAction()},
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(IconSize.l)
        )
    }
}

@DFPreview
@Composable
fun DFActionButtonPrimaryPreview() {
    DeckFlowTheme {
        DFActionButtonPrimary(
            icon = Icons.Default.PlayArrow,
            contentDescription = ""
        ) { }
    }
}