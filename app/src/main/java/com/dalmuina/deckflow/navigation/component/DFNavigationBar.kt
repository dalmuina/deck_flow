package com.dalmuina.deckflow.navigation.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.deckflow.navigation.Route
import com.dalmuina.deckflow.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun DFNavigationBar(
    modifier: Modifier = Modifier,
    selectedKey: NavKey,
    onSelectedKey: (NavKey) -> Unit,
) {
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0,0,0,0),
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { (topLevelDestination, data) ->
                NavigationBarItem(
                    selected = selectedKey == topLevelDestination,
                    onClick = { onSelectedKey(topLevelDestination) },
                    icon = {
                        Icon(
                            imageVector = data.icon,
                            contentDescription = data.name
                        )
                    },
                    label = { Text(text = data.name) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
            }
        }
    }
}

@DFPreview
@Composable
fun DFNavigationBarPreview() {
    DeckFlowTheme {
        DFNavigationBar(
            selectedKey = Route.Card
        ) { }
    }
}