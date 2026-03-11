package com.dalmuina.deckflow.navigation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.dalmuina.deckflow.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun DFNavigationBar(
    modifier: Modifier = Modifier,
    selectedKey: NavKey,
    onSelectedKey: (NavKey) -> Unit,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (topLevelDestination, data) ->
            NavigationBarItem(
                selected = selectedKey == topLevelDestination,
                onClick = {
                    onSelectedKey(topLevelDestination)
                },
                icon = {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.name
                    )
                },
                label = {
                    Text(text = data.name)
                }
            )
        }
    }

}