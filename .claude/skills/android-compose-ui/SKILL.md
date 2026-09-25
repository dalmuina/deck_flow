---
name: android-compose-ui
description: |
  Compose UI patterns for Android/KMP - stability, recomposition, side effects, lazy lists, animations, previews, accessibility, modifier extensions, and design system composables. Use this skill whenever writing or reviewing composables, optimizing recomposition, adding animations, creating previews, writing custom modifiers, structuring a design system, or making any Compose UI decision beyond the MVI/ViewModel layer. Trigger on phrases like "composable", "recomposition", "LaunchedEffect", "Modifier", "LazyColumn", "preview", "animation", "design system", "stability", "contentDescription", "graphicsLayer", "slot API", or "Compose performance".
---

# Android / KMP Compose UI Patterns

## Core Principle

The UI is dumb. Composables render state and forward user actions — nothing more. All state lives in the ViewModel. Compose code should contain zero business logic, zero data transformation, and minimal side effects.

---

## Strings — No Hardcoding

**Never hardcode user-visible text in composables.** Always use `stringResource()`.

```kotlin
// Bad
Text("Delete Card")
DFConfirmDialog(title = "Delete Card", message = "Are you sure you want to delete ${card.name}?")

// Good
Text(stringResource(R.string.deck_creator_delete_card_title))
DFConfirmDialog(
    title = stringResource(R.string.deck_creator_delete_card_title),
    message = stringResource(R.string.deck_creator_delete_card_message, card.name),
)
```

**Where to put strings:**

- **Feature-specific strings** → feature module's own `res/values/strings.xml` (e.g., `feature-deck/src/main/res/values/strings.xml`)
- **Generic/shared strings** (Cancel, Confirm, OK, Error) → `:core:design-system` or `:core:presentation` `res/values/strings.xml`

Name strings with a feature prefix to avoid collisions: `deck_creator_delete_card_title`, `card_creator_save_button`.

Never place feature strings in `:app` — feature modules cannot access `:app` resources.

---

## Dimensions — Use Design System Tokens

**Never hardcode `dp` values in composables.** Always use the token objects from `:core:design-system`.

```kotlin
// Bad
Modifier.padding(16.dp)
Modifier.size(36.dp)
Card(shape = RoundedCornerShape(24.dp))

// Good
Modifier.padding(Spacing.l)
Modifier.size(Dimens.mediumIcons)
Card(shape = RoundedCornerShape(Dimens.mediumCorner))
```

Use `Spacing` for padding, margins, gaps, and layout spacing (`xxs`=2, `xs`=4, `s`=8, `m`=12, `l`=16, `xl`=32, `xxl`=64 dp). Use `Dimens` for component-specific sizes (heights, elevations, icon sizes, corner radii) — all defined in `:core:design-system`.

When a value doesn't fit any existing token, add it to the appropriate object in `:core:design-system` with a comment explaining what it represents. Never add a one-off `dp` value inline in a composable.

---

## Stability & Recomposition

Strong skipping mode is enabled by default — no explicit opt-in needed.

Only annotate a state class with `@Stable` when it contains fields the Compose compiler considers unstable (e.g., `List`, `Map`, interfaces). Primitives and `String` are stable by default:

```kotlin
// Needs @Stable — contains a List
@Stable
data class NoteListState(val notes: List<NoteUi> = emptyList(), val isLoading: Boolean = false)

// No annotation needed — all fields are stable
data class NoteDetailState(val title: String = "", val body: String = "", val isSaving: Boolean = false)
```

---

## State Ownership

All state lives in the ViewModel. Do not use `remember` or `rememberSaveable` for application state — use `collectAsStateWithLifecycle()`:

```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()
```

The only exception is Compose-internal state the framework requires in composition (`LazyListState`, `ScrollState`, `PagerState`). For these, use `remember*` as needed. Use `derivedStateOf` only when Compose-internal state drives a derived value — if the derivation can happen in the ViewModel, it should.

---

## Side Effects

Prefer handling side effects through the ViewModel via an Intent. When a side effect is truly necessary (e.g., lifecycle API with no ViewModel equivalent), extract it into a dedicated composable:

```kotlin
@Composable
fun ObserveLifecycle(onStart: () -> Unit, onStop: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
```

Do not use custom `CompositionLocal`s.

---

## Lazy Layouts

Add `key` to lazy list items when there is an obvious unique identifier:

```kotlin
LazyColumn {
    items(items = state.notes, key = { it.id }) { note ->
        NoteItem(note = note, onClick = { onIntent(NoteListIntent.OnNoteClick(note.id)) })
    }
}
```

---

## Animations

Prefer approaches that animate below the recomposition layer:

```kotlin
// Good — animates without recomposition
val alpha by animateFloatAsState(if (state.isVisible) 1f else 0f)
Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })

// Bad — causes recomposition on every frame
Box(modifier = Modifier.alpha(animatedAlpha))
```

**Deferred state reads** — pass state as a lambda to defer the read to the draw phase:

```kotlin
// Good — deferred read
fun Modifier.animatedOffset(offsetProvider: () -> IntOffset) = offset { offsetProvider() }

// Bad — immediate read causes recomposition
fun Modifier.animatedOffset(offset: IntOffset) = offset(x = offset.x.dp, y = offset.y.dp)
```

---

## Modifier Extensions

Prefer plain `Modifier` extension functions or `Modifier.Node`-based factories. Do not make modifier extensions `@Composable`:

```kotlin
fun Modifier.roundedBackground(color: Color, radius: Dp) =
    background(color, RoundedCornerShape(radius))
```

---

## Design System & Slot APIs

The design system lives in `:core:design-system`. It provides reusable components, colors, theme, typography, and token objects. Follow the **naming-class** skill for component names (e.g., `DFOutlinedButton`, `DFTopBar`).

Use a slot API (`@Composable () -> Unit` parameters) for design system components that need flexible content. Feature-level composables prefer typed parameters over slots for clarity.

---

## Previews

Every Screen composable needs at least one `@Preview` with realistic state. Use the project's custom Preview annotation from `:core:design-system` (follows the **naming-class** skill, e.g. `@DFPreview`). If it doesn't exist yet, create it:

```kotlin
@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_7)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_7)
annotation class DFPreview
```

Usage:
```kotlin
@DFPreview
@Composable
private fun NoteListScreenPreview() {
    AppTheme {
        NoteListScreen(
            state = NoteListState(notes = listOf(NoteUi("1", "Meeting notes", "Mar 15"))),
            onIntent = {}
        )
    }
}
```

---

## Accessibility

Use meaningful `contentDescription` on all interactive or informational visual elements. Always use string resources:

```kotlin
Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete_note))
```

For decorative elements, set `contentDescription = null`. Use `Modifier.semantics { }` only when Compose or Material 3 does not provide the semantic information automatically:

```kotlin
Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
    Icon(imageVector = Icons.Default.Star, contentDescription = null)
    Text(text = stringResource(R.string.label_favorite))
}
```

---

## TextField

Text input state lives in the ViewModel. Every keystroke dispatches an Intent:

```kotlin
TextField(
    value = state.title,
    onValueChange = { onIntent(NoteEditorIntent.OnTitleChange(it)) },
)
```
