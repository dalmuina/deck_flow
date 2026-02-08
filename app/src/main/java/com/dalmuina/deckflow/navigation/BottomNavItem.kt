package com.dalmuina.deckflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

class BottomNavItem(
    val icon: ImageVector,
    val title: String,
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.Card to BottomNavItem(Icons.Outlined.Home, "Card"),
    Route.DeckSelector to BottomNavItem(Icons.Outlined.ContentPaste, "Deck"),
)