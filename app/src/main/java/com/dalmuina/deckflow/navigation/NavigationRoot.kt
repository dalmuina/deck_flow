package com.dalmuina.deckflow.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dalmuina.UiEvent
import com.dalmuina.UiEventDispatcher
import com.dalmuina.deckflow.navigation.component.DFNavigationBar
import com.dalmuina.designsystem.animation.DFAnimations
import com.dalmuina.designsystem.component.button.DFFloatingButton
import com.dalmuina.feature.card.ui.CardRoute
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorRoute
import com.dalmuina.feature.deck.ui.deckCreator.DeckCreatorRoute
import com.dalmuina.feature.deck.ui.deckSelector.DeckSelectorRoute
import org.koin.compose.koinInject

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    uiEventDispatcher: UiEventDispatcher = koinInject()
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.DeckSelector,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )
    val navigator = remember {
        Navigator(navigationState)
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val context by rememberUpdatedState(LocalContext.current)

    @SuppressLint("LocalContextGetResourceValueCall")
    LaunchedEffect(Unit) {
        uiEventDispatcher.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {

                    val message = context.getString(event.messageRes)

                    val action = event.actionLabelRes?.let {
                        context.getString(it)
                    }

                    snackBarHostState.showSnackbar(
                        message = message,
                        actionLabel = action
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
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
                    entry<Route.CardCreator> {
                        CardCreatorRoute {
                            navigator.goBack()
                        }
                    }
                }
            )
        )
    }

}

@Composable
fun FabArea(
    state: NavigationState,
    navigate: (Route) -> Unit,
) {
    val showFab = when (state.currentRoute) {
        is Route.DeckSelector -> true
        is Route.Card -> true
        is Route.DeckCreator -> true
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
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Deck"
                        )
                    }
                ) {
                    navigate(Route.DeckCreator)
                }
            }

            Route.Card -> {
                DFFloatingButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Deck"
                        )
                    }
                ) {

                }
            }

            Route.DeckCreator -> {
                DFFloatingButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Card"
                        )
                    }
                ) {
                    navigate(Route.CardCreator)
                }
            }

            else -> Unit
        }
    }
}
