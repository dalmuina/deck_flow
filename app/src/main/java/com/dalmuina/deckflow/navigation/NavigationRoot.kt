package com.dalmuina.deckflow.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dalmuina.deckflow.navigation.component.DFNavigationBar
import com.dalmuina.designsystem.animation.DFAnimations
import com.dalmuina.designsystem.component.button.DFFloatingButton
import com.dalmuina.feature.card.ui.CardRoute
import com.dalmuina.feature.deck.ui.deckCreator.DeckCreatorRoute
import com.dalmuina.feature.deck.ui.deckSelector.DeckSelectorRoute

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.DeckSelector,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )
    val navigator = remember {
        Navigator(navigationState)
    }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            DFNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectedKey = {
                    navigator.navigate(it)
                })
        },
        floatingActionButton = {
            FabArea(navigationState) {
                navigator.navigate(it)
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider = entryProvider {
                    entry<Route.Card> {
                        CardRoute()
                    }
                    entry<Route.DeckSelector> {
                        DeckSelectorRoute(
                            onAddDeck = {
                                navigator.navigate(Route.DeckCreator)
                            }
                        )
                    }
                    entry<Route.DeckCreator> {
                        DeckCreatorRoute()
                    }
                }
            )
        )
    }

}

@Composable
fun FabArea(
    state: NavigationState,
    navigate: (Route)->Unit,
) {
    val showFab = when (state.currentRoute) {
        is Route.DeckSelector -> true
        is Route.Card -> true
        else -> false
    }

    AnimatedVisibility(
        visible = showFab,
        enter = DFAnimations.ScaleFadeIn,
        exit = DFAnimations.ScaleFadeOut
    ) {
        when (state.currentRoute) {
            Route.DeckSelector -> {
                DFFloatingButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Create deck"
                ) {
                    navigate(Route.DeckCreator)
                }
            }

            Route.Card -> {
                DFFloatingButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Add deck"
                ) {

                }
            }

            else -> Unit
        }
    }
}
