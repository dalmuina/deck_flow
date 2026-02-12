package com.dalmuina.deckflow.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.dalmuina.designsystem.components.buttons.DeckFlowFloatingButton
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
            DeckFlowNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectedKey = {
                    navigator.navigate(it)
                })
        },
        floatingActionButton = {

            val showFab = when (navigationState.currentRoute) {
                is Route.DeckSelector -> true
                is Route.Card -> true
                else -> false
            }

            AnimatedVisibility(
                visible = showFab,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                when (navigationState.currentRoute) {
                    Route.DeckSelector -> {
                        DeckFlowFloatingButton(
                            icon = Icons.Default.Add,
                            contentDescription = "Create deck"
                        ) {
                            navigator.navigate(Route.DeckCreator)
                        }
                    }

                    Route.Card -> {
                        DeckFlowFloatingButton(
                            icon = Icons.Default.Add,
                            contentDescription = "Add deck"
                        ) {

                        }
                    }

                    else -> Unit
                }
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
