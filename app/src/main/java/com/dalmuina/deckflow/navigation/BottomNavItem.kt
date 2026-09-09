package com.dalmuina.deckflow.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material.icons.outlined.Style
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.dalmuina.deckflow.R

@Immutable
class BottomNavItem(
    val icon: ImageVector,
    @StringRes val nameRes: Int,
)

val TOP_LEVEL_DESTINATIONS =
    mapOf(
        Route.Card to BottomNavItem(Icons.Outlined.Schedule, R.string.nav_tab_routine),
        Route.DeckSelector to BottomNavItem(Icons.Outlined.Style, R.string.nav_decks),
        Route.Stats to BottomNavItem(Icons.Outlined.StackedBarChart, R.string.nav_stats),
    )
