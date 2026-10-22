package com.dalmuina.core.design_system.component.badge

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.theme.PostponeContainer
import com.dalmuina.core.design_system.theme.SuccessContainer
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing

@Composable
fun DFBadgeSwipe(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color,
    alpha: Float = 1f,
    rotation: Float = 0f,
) {
    // Gradient fades toward a lighter tint so white text keeps >= 5:1 contrast on both stops
    val gradient =
        Brush.horizontalGradient(
            colors =
                listOf(containerColor, lerp(containerColor, Color.White, 0.15f))
                    .map { it.copy(alpha = alpha) },
        )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier =
            modifier
                .rotate(rotation)
                .clip(RoundedCornerShape(Corner.m))
                .background(gradient)
                .padding(horizontal = Spacing.l, vertical = Spacing.m),
    ) {
        Text(
            text = text,
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                ),
            color = Color.White.copy(alpha = alpha),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@DFPreview
@Composable
fun DFBadgeSwipeCompletedPreview() {
    DeckFlowTheme {
        DFBadgeSwipe(
            text = "POSTPONED",
            containerColor = PostponeContainer,
        )
    }
}

@DFPreview
@Composable
fun DFBadgeSwipePostponedPreview() {
    DeckFlowTheme {
        DFBadgeSwipe(
            text = "COMPLETED",
            containerColor = SuccessContainer,
        )
    }
}
