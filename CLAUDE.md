# DeckFlow — Android Project

@SPEC.md
@ARCHITECTURE.md

## Working Principles

**Think before coding.** State your assumptions out loud. If the request is ambiguous, ask. If a simpler approach exists, push back. Stop when you are confused, name what is unclear, do not just pick one interpretation and run.

**Simplicity first.** Write the minimum code that solves the problem. No speculative abstractions. No flexibility nobody asked for. Idiomatic over clever — a reader unfamiliar with the feature should follow it without effort.

**Surgical changes.** Touch only what the task requires. Do not improve neighboring code. Do not refactor what is not broken. Every changed line should trace back to the request. If you spot a defect in untouched code, flag it — do not fix it silently.

**Goal-driven execution.** Turn vague instructions into verifiable targets before writing a line. "Add validation" becomes "write tests for invalid inputs, then make them pass."

---

## Build

```
./gradlew assembleDebug       # build debug
./gradlew test                # unit tests
./gradlew ktlintCheck         # lint
```

---

## Conventions

- Project prefix: **DF** → `DFResult`, `DFError`, `DFPreview`, `DFNavigationBar`…
- **Never** use `Impl` suffix — name by what makes the class unique
- `@Stable` on all State and UI model data classes
- `@Immutable` on UI model lists
- `IO` dispatcher injected with `named("IO")` in use cases
- Check `SPEC.md` for what to build and `ARCHITECTURE.md` for where it goes, then follow the skills in `.claude/skills/` for architecture, naming, and patterns
