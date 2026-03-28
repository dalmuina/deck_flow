package com.dalmuina.deckflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material.icons.outlined.Style
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
@Immutable
class BottomNavItem(
    val icon: ImageVector,
    val name: String,
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.Card to BottomNavItem(Icons.Outlined.Schedule , "Routine"),
    Route.DeckSelector to BottomNavItem(Icons.Outlined.Style, "Decks"),
    Route.Stats to BottomNavItem(Icons.Outlined.StackedBarChart, "Stats")
)