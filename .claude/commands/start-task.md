Description
Start a new task following the SPEC→PLAN→CODE→REVIEW loop

Agent
build

@AGENTS.md @SPEC.md @ARCHITECTURE.md

Please act as the Orchestrator. Start the following task: $ARGUMENTS

Follow the SPEC→PLAN→CODE→REVIEW→COMMIT loop as defined in AGENTS.md,
including its per-dispatch logging (PROMPT_LOG.md), per-task rollup
(EFFICIENCY.md), and build-verification-before-commit gate — all apply
automatically via CLAUDE.md, this command doesn't need to repeat the
mechanics:

Confirm the task against SPEC.md and ARCHITECTURE.md before planning.
Present a plan (files to touch, approach, tests) and stop at the PLAN gate for explicit human approval before writing any code.
Respect the module boundaries in ARCHITECTURE.md and the conventions in CLAUDE.md.
Use Conventional Commits for the commit message.
After committing, if the next likely task is unrelated to this one, suggest running /new or /compact before continuing.