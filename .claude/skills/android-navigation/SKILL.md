---
name: android-navigation
description: |
  Navigation3-based navigation for Android - Route sealed interface, Navigator, NavigationState, NavDisplay, and centralized wiring in :app. Use this skill whenever setting up navigation, defining routes, adding a new screen, managing back stacks, or wiring the navigation host. Trigger on phrases like "set up navigation", "add a route", "navigate between screens", "NavDisplay", "Navigator", "NavigationState", "entryProvider", "top-level route", "back stack", or "cross-feature navigation".
---

# Android Navigation (Navigation3)

## Principles

- All routes are defined in `:app` as a single `@Serializable sealed interface Route : NavKey`.
- All navigation is controlled from `:app` — feature modules have zero knowledge of the nav graph.
- Navigation **within** a feature is handled via callbacks passed into the feature's Route composable.
- Feature-to-feature navigation uses callbacks at the `NavigationRoute` level — never direct route imports in feature modules.
- Back navigation is always a callback (`onBack: () -> Unit`), never a `NavController` reference.

---

## Routes (`:app`)

```kotlin
@Serializable
sealed interface Route : NavKey {
    @Serializable data object Card : Route
    @Serializable data object DeckSelector : Route
    @Serializable data class DeckCreator(val mode: DeckCreatorMode) : Route
    @Serializable data class CardCreator(val mode: CardCreatorMode) : Route
    @Serializable data object Stats : Route
}

fun Route.title(): String = when (this) {
    Route.Card -> "Cards"
    Route.DeckSelector -> "Decks"
    is Route.DeckCreator -> "Create Deck"
    is Route.CardCreator -> "Create Card"
    Route.Stats -> "Stats"
}
```

---

## Navigator and NavigationState (`:app`)

`NavigationState` holds one back stack per top-level destination. `Navigator` mutates it:

- `navigate(route)` — if top-level, switches to it; otherwise pushes onto the current back stack.
- `goBack()` — pops the current back stack, or returns to `startRoute` if at top.

Feature modules never import or reference these classes.

---

## Top-Level Destinations (`:app`)

```kotlin
val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.Card to BottomNavItem(Icons.Outlined.Schedule, "Routine"),
    Route.DeckSelector to BottomNavItem(Icons.Outlined.Style, "Decks"),
    Route.Stats to BottomNavItem(Icons.Outlined.StackedBarChart, "Stats"),
)
```

---

## NavigationRoute (`:app`)

All screens are registered in a single `entryProvider` block. This is the only place routes map to composables. Cross-feature result passing (e.g., `createdCardId`) is managed here:

```kotlin
entry { backStackEntry ->
    DeckCreatorRoute(
        mode = backStackEntry.mode,
        createdCardId = createdCardId,
        onCreatedCardConsumed = { createdCardId = null },
        onEditCard = { cardId -> navigator.navigate(Route.CardCreator(CardCreatorMode.Edit(cardId))) },
        onBack = { navigator.goBack() },
    )
}

entry { backStackEntry ->
    CardCreatorRoute(
        mode = backStackEntry.mode,
        onCardSaved = { cardId -> createdCardId = cardId },
        onDismiss = { navigator.goBack() },
    )
}
```

Global UI concerns (snackbars, top bar, bottom bar) are owned by `NavigationRoute` via `uiEventDispatcher` — see the **android-presentation-mvi** skill.

---

## Feature Route Composables

Each screen exposes a Route composable in its `presentation` module. It receives the ViewModel and navigation callbacks — never imports `Navigator`, `Route`, or any `:app` class. See **android-presentation-mvi** for the full Route composable pattern.

---

## Passing Arguments

Pass serializable values directly in the route data class. For screen results (e.g., a created card ID), use a shared `var` in `NavigationRoute` state:

```kotlin
var createdCardId by remember { mutableStateOf<Int?>(null) }
```

Never pass complex domain objects through routes.

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Route entry | nested in `Route` sealed interface | `Route.DeckCreator`, `Route.Card` |
| Feature entry composable | `<Feature>Route` | `DeckCreatorRoute` |
| Feature screen composable | `<Feature>Screen` | `DeckCreatorScreen` |
| Navigation host | `NavigationRoute` | — |

---

## Checklist: Adding a New Screen

- [ ] Add a `@Serializable` entry to `Route` in `:app`
- [ ] Register it in `NavigationRoute`'s `entryProvider` block with all navigation callbacks
- [ ] If it needs a bottom nav tab, add it to `TOP_LEVEL_DESTINATIONS`
- [ ] Create a `Route` composable in `:presentation` — accepts callbacks, no `:app` imports
- [ ] Create a `Screen` composable — pure state + `onIntent`, previewable
- [ ] Pass back navigation as `onBack: () -> Unit` — never pass `Navigator` into a feature
