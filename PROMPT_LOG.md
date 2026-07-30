# PROMPT_LOG.md — Per-dispatch prompt log

Detailed, append-only log of every prompt one agent hands to another —
human → Orchestrator, Orchestrator → Reviewer, Orchestrator → an
escalated model, Orchestrator → a Task-tool subagent, or the Orchestrator
moving itself into a new phase. Logged as it happens, with the exact
date/time. See AGENTS.md section 1 ("Per-dispatch logging") and section 5.

EFFICIENCY.md stays the per-task summary; this file is the detailed trail
underneath it — each EFFICIENCY.md row should be traceable back to the
PROMPT_LOG.md rows for that task via the "Linked task" column.

Token/cost figures follow the same rule as EFFICIENCY.md: always the
human-reported session status bar reading, taken immediately before and
after the dispatch — never estimated or fabricated by the agent itself.

| Date/Time | From → To | Prompt | Tokens (Δ) | Est. cost (Δ) | Linked task (EFFICIENCY.md) | Notes |
|---|---|---|---|---|---|---|
