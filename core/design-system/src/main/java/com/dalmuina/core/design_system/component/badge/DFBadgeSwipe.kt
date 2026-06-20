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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.theme.SuccessContainer
import com.dalmuina.core.design_system.theme.Success
import com.dalmuina.core.design_system.theme.PostponeContainer
import com.dalmuina.core.design_system.theme.Postpone
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.core.design_system.tokens.Stroke

@Composable
fun DFBadgeSwipe(
    modifier: Modifier = Modifier,
    text: String,
    strokeColor: Color,
    alpha: Float = 1f,
    rotation: Float = 0f,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .rotate(rotation)
            .clip(RoundedCornerShape(Corner.m))
            .border(
                width = Stroke.l,
                color = strokeColor.copy(alpha = alpha),
                shape = RoundedCornerShape(Corner.m)
            )
            .padding(horizontal = Spacing.l, vertical = Spacing.s)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.10.sp,
            ),
            color = Color.White.copy(alpha = alpha),
        )
    }
}

@DFPreview
@Composable
fun DFBadgeSwipeCompletedPreview() {
    DeckFlowTheme {
        DFBadgeSwipe(
            text = "← Postponed",
            strokeColor = PostponeContainer,
        )
    }
}

@DFPreview
@Composable
fun DFBadgeSwipePostponedPreview() {
    DeckFlowTheme {
        DFBadgeSwipe(
            text = "Completed ✓",
            strokeColor = SuccessContainer,
        )
    }
}