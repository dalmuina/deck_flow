package com.dalmuina.feature.card.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.R

@Composable
fun DFStreakBadge(
    modifier: Modifier = Modifier,
    streakDays: Int,
) {
    val badgeColor = Color.White.copy(alpha = 0.22f)
    val text =
        if (streakDays > 0) {
            stringResource(R.string.streak_days, streakDays)
        } else {
            stringResource(R.string.streak_none)
        }

    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(20.dp))
                .background(badgeColor)
                .padding(horizontal = Spacing.m, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (streakDays > 0) "🔥 $text" else text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
    }
}

@DFPreview
@Composable
fun DFStreakBadgeActivePreview() {
    DeckFlowTheme {
        DFStreakBadge(streakDays = 7)
    }
}

@DFPreview
@Composable
fun DFStreakBadgeNonePreview() {
    DeckFlowTheme {
        DFStreakBadge(streakDays = 0)
    }
}
