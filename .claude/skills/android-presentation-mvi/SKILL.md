---
name: android-presentation-mvi
description: |
  MVI presentation layer for Android/KMP - State, Intent, Event, ViewModel, Route/Screen composable split, UI models, UiText error mapping, and process death with SavedStateHandle. Use this skill whenever creating or reviewing a ViewModel, defining screen state, intents, or events, structuring composables, mapping errors to UI strings, or handling process death. Trigger on phrases like "add a ViewModel", "create a screen", "MVI", "state", "intent", "event", "screen composable", "UiText", "SavedStateHandle", or "UI model".
---

# Android / KMP Presentation Layer (MVI)

## Overview

Every screen has:

1. **State** — a single data class holding all UI state fields.
2. **Intent** — a sealed interface of all user-triggered actions.
3. **Event** — a sealed interface of one-time side effects (navigation, snackbar).
4. **ViewModel** — holds `StateFlow`, processes `Intent`, emits `Event` via `MutableSharedFlow`.

---

## State

```kotlin
data class NoteListState(
    val notes: List<NoteUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
```

Always update with `.update { }` — never replace the entire flow:
```kotlin
_state.update { it.copy(isLoading = true) }
```

---

## Intent

```kotlin
sealed interface DeckCreatorIntent {
    data class SelectedCard(val id: Int) : DeckCreatorIntent
    data class CardCreated(val id: Int) : DeckCreatorIntent
    data object SaveDeck : DeckCreatorIntent
    data class NameChanged(val value: String) : DeckCreatorIntent
    data class DeleteCard(val id: Int) : DeckCreatorIntent
    data class Reorder(val from: Int, val to: Int) : DeckCreatorIntent
}
```

---

## Event (one-time side effects)

Events use `MutableSharedFlow` — not `Channel`:

```kotlin
sealed interface DeckCreatorEvent {
    data object CloseScreen : DeckCreatorEvent
}

// In ViewModel:
private val _events = MutableSharedFlow<DeckCreatorEvent>()
val events = _events.asSharedFlow()

// Emit:
viewModelScope.launch { _events.emit(DeckCreatorEvent.CloseScreen) }
```

---

## ViewModel

Entry point is `process(intent)`. State is exposed as `uiState`. Two patterns depending on whether state is imperative or derived from flows.

### Pattern A — Manual state (pure MVI)

Use when state is updated imperatively in response to intents:

```kotlin
class CardCreatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CardCreatorState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CardCreatorEvent>()
    val events = _events.asSharedFlow()

    fun process(intent: CardCreatorIntent) {
        when (intent) {
            is CardCreatorIntent.NameChanged -> reduce { copy(name = intent.value) }
            CardCreatorIntent.Save -> save()
        }
    }

    private inline fun reduce(reducer: CardCreatorState.() -> CardCreatorState) {
        _uiState.update { it.reducer() }
    }
}
```

### Pattern B — Reactive state (`combine` + `stateIn`)

Use when state derives from multiple flows (e.g., DB list combined with local selection state):

```kotlin
val uiState: StateFlow<DeckCreatorState> =
    combine(cardsUiFlow, selectedCards, deckName) { cards, selected, name ->
        val orderMap = selected.associate { it.id to it.order }
        DeckCreatorState(
            loading = false,
            deckCards = cards.map { card ->
                card.copy(
                    isSelected = orderMap.containsKey(card.id),
                    order = orderMap[card.id],
                )
            }.sortedWith(compareBy { !it.isSelected }.thenBy { it.order ?: Int.MAX_VALUE }),
            name = name
        )
    }
    .onStart { emit(DeckCreatorState(loading = true)) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_SUBSCRIPTION),
        initialValue = DeckCreatorState(loading = true),
    )

fun process(intent: DeckCreatorIntent) {
    when (intent) {
        is DeckCreatorIntent.NameChanged -> deckName.value = intent.value
        is DeckCreatorIntent.SelectedCard -> selectedCard(intent.id)
    }
}
```

---

## Snackbars and Global UI Events

Route snackbars through `uiEventDispatcher`, not through the screen's own `events` flow:

```kotlin
is Result.Error -> {
    uiEventDispatcher.dispatch(UiEvent.ShowSnackBar(result.error.toUiText()))
}
```

Use screen `events` only for screen-specific lifecycle concerns (e.g., `CloseScreen`, `NavigateToDetail`).

---

## Composable Structure

### Route Composable

Collects `uiState` with `collectAsStateWithLifecycle()`. Observes events with `LaunchedEffect` + `collect`:

```kotlin
@Composable
fun DeckCreatorRoute(
    onBack: () -> Unit,
    createdCardId: Int?,
    onCreatedCardConsumed: () -> Unit,
    viewModel: DeckCreatorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DeckCreatorEvent.CloseScreen -> onBack()
            }
        }
    }

    LaunchedEffect(createdCardId) {
        createdCardId?.let { cardId ->
            viewModel.process(DeckCreatorIntent.CardCreated(cardId))
            onCreatedCardConsumed()
        }
    }

    DeckCreatorScreen(state = state, onIntent = viewModel::process)
}
```

### Screen Composable

Receives `state` and `onIntent`. No ViewModel reference:

```kotlin
@Composable
fun DeckCreatorScreen(
    state: DeckCreatorState,
    onIntent: (DeckCreatorIntent) -> Unit
) { ... }
```

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| ViewModel | `<Feature>ViewModel` | `DeckCreatorViewModel` |
| State | `<Feature>State` | `DeckCreatorState` |
| Intent | `<Feature>Intent` | `DeckCreatorIntent` |
| Event | `<Feature>Event` | `DeckCreatorEvent` |
| State property | `uiState` | `val uiState: StateFlow<...>` |
| Entry point | `process(intent)` | `fun process(intent: DeckCreatorIntent)` |
| Route composable | `<Feature>Route` | `DeckCreatorRoute` |
| Screen composable | `<Feature>Screen` | `DeckCreatorScreen` |
| UI model | `<Entity>Ui` | `CardUi` |

---

## Checklist: Adding a New Screen

- [ ] Define `State`, `Intent`, `Event` in `feature:presentation`
- [ ] Choose Pattern A (manual `reduce`) or Pattern B (`combine` + `stateIn`)
- [ ] Implement `ViewModel` with `process(intent)`, `uiState`, `events`
- [ ] Create `Route` composable — collects state, observes events via `LaunchedEffect`
- [ ] Create `Screen` composable — pure state + `onIntent`, previewable
- [ ] Route snackbars through `uiEventDispatcher`
- [ ] Map domain errors to `UiText` via extension functions
- [ ] Add `SavedStateHandle` for form fields that must survive process death
