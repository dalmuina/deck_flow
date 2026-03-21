package com.dalmuina.designsystem.component.badge

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.dalmuina.designsystem.tokens.Spacing

@Composable
fun DFSwipeBadge(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    alpha: Float = 1f,
) {
    Box(
        modifier = modifier
            .alpha(alpha)
            .border(
                width = Spacing.xxs,
                color = color,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = Spacing.l, vertical = Spacing.l)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}