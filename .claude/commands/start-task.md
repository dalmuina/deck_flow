Description
Start a new task following the SPEC→PLAN→CODE→REVIEW loop

Agent
build

@AGENTS.md @SPEC.md @ARCHITECTURE.md @rules.md

Please act as the Orchestrator. Start the following task: $ARGUMENTS

STOP — mandatory first step, before reading files, planning, or anything else: ask the human for the current session status bar reading (tokens and estimated cost) as the baseline for this task. Do not proceed to SPEC confirmation or planning until you have this baseline. Never estimate or fabricate it.

Follow the SPEC→PLAN→CODE→REVIEW loop as defined in AGENTS.md:

Confirm the task against SPEC.md and ARCHITECTURE.md before planning.
Present a plan (files to touch, approach, tests) and stop at the PLAN gate for explicit human approval before writing any code.
Respect the component boundaries in ARCHITECTURE.md and the rules in rules.md.
After REVIEW, on PASS or an accepted WARNING: before committing, ask the human again for the current session status bar reading (tokens and estimated cost) — never estimate or fabricate this figure. Compute this task's delta as (end reading − baseline reading). If the human mentions a /new or /compact happened between baseline and now, do not compute a delta — log the end reading as an absolute value and note the reset explicitly instead.
Append a row to EFFICIENCY.md with the computed (or noted-as-reset) figures.
Use Conventional Commits for the commit message.
After committing, if the next likely task is unrelated to this one, suggest running /new or /compact before continuing.