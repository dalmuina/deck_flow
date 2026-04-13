package com.dalmuina.core.design_system.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.Amber400
import com.dalmuina.core.design_system.theme.Amber700
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.theme.Green500
import com.dalmuina.core.design_system.theme.Green800
import com.dalmuina.core.design_system.tokens.Stroke
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing

@Composable
fun DFSwipeBadge(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    strokeColor: Color,
    alpha: Float = 1f,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = modifier
            .clip(RoundedCornerShape(Corner.xs))
            .border(
                width = Stroke.m,
                color = strokeColor.copy(alpha = alpha),
                shape = RoundedCornerShape(Corner.xs)
            )
            .background(color = backgroundColor.copy(alpha = alpha))
            .padding(horizontal = Spacing.m, vertical = Spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = alpha),
        )
    }
}

@DFPreview
@Composable
fun DFSwipeBadgeCompletedPreview() {
    DeckFlowTheme {
        DFSwipeBadge(
            text = "← Postponed",
            backgroundColor = Amber400,
            strokeColor = Amber700,
        )
    }
}

@DFPreview
@Composable
fun DFSwipeBadgePostponedPreview() {
    DeckFlowTheme {
        DFSwipeBadge(
            text = "Completed ✓",
            backgroundColor = Green500,
            strokeColor = Green800,
        )
    }
}