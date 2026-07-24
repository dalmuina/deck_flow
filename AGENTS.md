AGENTS.md — Instructions for AI Agents

This file defines how AI agents must operate on this repository: the
workflow they follow, the model assigned to each role, and the rules for
escalating when a model's output isn't good enough.

1. Workflow

All feature work follows this loop, driven by the Orchestrator:

SPEC  ->  PLAN  ->  CODE  ->  REVIEW  ->  COMMIT

- SPEC: confirm or update SPEC.md for the task at hand. Do not proceed
  to PLAN until acceptance criteria are explicit and testable.
- PLAN: break the task into concrete steps, list which files will be
  created/modified, and identify which skill(s) in `.claude/skills/`
  apply.
- CODE: implement the plan. Keep changes scoped to what the plan
  describes — no unrelated refactors bundled into the same change.
- REVIEW: hand the diff to the Reviewer agent (see .claude/agents/reviewer.md),
  which evaluates it against CLAUDE.md, ARCHITECTURE.md, SPEC.md, and the
  relevant skill(s), and returns PASS, WARNING, or FAIL with reasons.
- COMMIT: only on PASS (or WARNING explicitly accepted by the human).
  Before committing, ask the human to report the current session status bar
  reading (context tokens and estimated cost) — the Orchestrator has no
  direct way to read this value itself, so it must not estimate or
  fabricate it. Append a row to EFFICIENCY.md for the task just
  completed using that reported value: model(s) used, context tokens and
  cost as reported, escalations if any, and a short note. Commit messages
  follow Conventional Commits (feat:, fix:, docs:, chore:,
  test:). After committing, if the next task is unrelated to the one
  just finished, explicitly suggest the human run /new (fresh session)
  or /compact (summarize, same session) before starting it.

The Orchestrator pauses for human approval at the end of each phase (a
"gate") before moving to the next one. Do not skip gates even when a task
looks trivial.

2. Agents

| Agent | Definition | Role |
|---|---|---|
| Orchestrator | `.claude/agents/orchestrator.md` | Drives the loop end-to-end, decides when to call the Reviewer, manages gates |
| Reviewer | `.claude/agents/reviewer.md` | Critiques diffs against CLAUDE.md/ARCHITECTURE.md/SPEC.md/skills; never writes implementation code |

3. Model assignment

| Task | Default model | Notes |
|---|---|---|
| Orchestration (planning, gate decisions, interpreting verdicts) | Claude Sonnet 4.6 | Requires multi-step reasoning and reliability |
| Code generation (ViewModels, use cases, repositories, DAOs, Compose UI) | Claude Haiku 4.5 | **Hard mandate, not a suggestion.** Every code-gen task starts here regardless of perceived complexity — do not preemptively pick a higher tier. The only path to a higher tier is the escalation rule below. |
| Code review (Reviewer agent) | Claude Sonnet 4.5 | Checklist-style evaluation against explicit conventions. Kept at Sonnet deliberately — a Haiku Reviewer risks rubber-stamping bad Haiku-written code as PASS, which would defeat the escalation safety net below. |
| Repetitive/mechanical work (extra fixtures, formatting, doc updates) | Claude Haiku 4.5 | Low-complexity, high-repetition; keep cost low |

This table is not a starting suggestion the Orchestrator can override by
judgment. Deviating from Haiku-first for code generation is only valid
through the documented escalation path in 3.1 — never a pre-emptive
choice, no matter how complex a task looks going in.

Verify these exact model names are enabled for this account before relying
on them — availability depends on organization-level model policies. If a
listed model isn't available, use the closest equivalent tier and record
the substitution in EFFICIENCY.md.

3.1 Escalation rule

Each agent uses its assigned default model. If the Reviewer returns FAIL
twice in a row for the same task or file, the Orchestrator must:

1. Retry that specific task once using the next model tier up
   (e.g. Haiku → Sonnet, Sonnet → Opus). This retry must be given the
   prior failed diff and the Reviewer's specific FAIL reasons/required
   changes as required input — the escalated model's task is "fix these
   specific issues in this diff," not "redo the task from the plan with
   no history of what was tried." This is what makes escalation actually
   cheaper than a cold restart.
2. If the retry also fails, stop and ask the human for guidance instead of
   attempting a third automated try.
3. Log every escalation in EFFICIENCY.md (task, original model, escalated
   model, outcome) — repeated escalations for the same kind of task signal
   that the default assignment in the table above needs revisiting.

4. Skills

Skills live in `.claude/skills/` and are loaded into an agent's context
for a specific recurring task (e.g. writing a ViewModel, wiring a Koin
module, adding a Room DAO). They do not run independently — they inform
whichever agent is currently active. See `CLAUDE.md`'s Conventions
section for the pointer to the skills index.

5. Conventions

- All documentation and code comments are written in English.
- Keep context loads minimal: reference specific files (@SPEC.md,
  @ARCHITECTURE.md) rather than pasting full contents into every prompt
  when only a section is relevant.
- Prefer short, focused sessions over long ones; use /clear or /compact
  between unrelated tasks to avoid unnecessary context carryover.
