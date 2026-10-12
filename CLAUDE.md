# StoreOps Harness Orchestrator

This file defines the orchestration contract for feature work in this repository. The
orchestrator coordinates the Planner, Generator, Evaluator, and Monitor agent prompts in
`.harness/agents/`. It must follow the approval gate, routing rules, and iteration limit
below; agent prompts do not override these rules.

## 1. Entry prompt format

The developer starts a run by providing a feature request in this format:

```text
Feature: <short feature name>
Goal: <user or business outcome>
Requirements:
- <required behavior>
Constraints:
- <architectural, compatibility, or operational constraint>
Acceptance criteria:
- GIVEN <initial context>, WHEN <action>, THEN <observable result>
Sprint boundary: <optional scope or priority guidance>
```

`Feature`, `Goal`, and at least one requirement are required. Constraints,
acceptance criteria, and sprint-boundary guidance may be supplied by the developer
or derived by the Planner. The orchestrator passes the complete request and relevant
repository rules to the Planner.

## 2. Agent prompts and invocation

Invoke an agent by loading only its prompt file and the inputs specified here. Each
invocation is a separate, bounded task; agents communicate through the named files,
not through an assumed shared conversation history.

| Agent | Prompt file | Invocation and responsibility |
| --- | --- | --- |
| Planner | `.harness/agents/planner-agent.md` | Invoke first with the developer request and relevant architecture, module-boundary, event-bus, error-contract, and test-coverage rules. It writes `spec.md` and `sprint-contract.md`. |
| Generator | `.harness/agents/generator-agent.md` | Invoke only after developer approval. Give it the approved `spec.md`, current `sprint-contract.md`, and relevant code/test context. It implements the contract, adds or updates tests, and writes `generator-summary.md`. |
| Evaluator | `.harness/agents/evaluator-agent.md` | Invoke after each Generator attempt. Give it the sprint acceptance criteria, changed files, and the checks/results available for that attempt. It runs/reviews the checks and writes `evaluator-feedback.md`. |
| Monitor | `.harness/agents/monitor-agent.md` | Invoke after each evaluation and at sprint completion/escalation. Give it the sprint name, iteration, verdict, and outcome. It records a timestamped run log under `.harness/reviews/<timestamp>/run-log.md`. |

The Generator summary must include an acceptance-criteria self-check table, files
changed, known gaps, and next-sprint suggestions. Evaluator feedback must include a
verdict (`PASS`, `CONDITIONAL PASS`, or `FAIL`), file/line-level feedback, and required
fixes. The Monitor records the verdict, escalation flag, estimated token cost, and
quality-trend notes where available.

## 3. Approval gate and run sequence

For each run, follow this sequence:

1. Invoke the Planner.
2. Confirm that `spec.md` and `sprint-contract.md` were produced. Present the spec and
   sprint contract to the developer.
3. The Planner output must end with the exact marker `AWAITING APPROVAL`. Do not invoke
   the Generator or make implementation changes before the developer approves.
4. Wait for the developer to type the exact text `APPROVED`. Any other response,
   including a request for changes, is not approval. On requested changes, return to
   the Planner to revise the spec and contract, then present them for approval again.
5. After approval, begin the Generator/Evaluator loop for the current sprint.
6. Once the sprint passes, invoke the Monitor and either start the next planned sprint
   or finish the run if no sprint remains.

## 4. Evaluator verdict routing and iteration limit

An iteration is one Generator invocation followed by one Evaluator invocation. Start
iteration numbering at 1 for each sprint. Read the latest `Verdict:` from
`evaluator-feedback.md` after every evaluation and route as follows:

| Verdict | Orchestrator action |
| --- | --- |
| `PASS` | Accept the sprint, invoke the Monitor, and start the next planned sprint if there is one. Otherwise mark the run complete. |
| `CONDITIONAL PASS` | Treat the listed required fixes as blocking for this sprint. If iterations remain, pass the feedback to the Generator for a focused retry, then evaluate again. Do not advance the sprint until a later evaluation returns `PASS`. |
| `FAIL` | Pass the evaluator's required fixes to the Generator for a focused retry if iterations remain, then evaluate again. |
| Missing, malformed, or unrecognized verdict | Do not treat it as success. Ask the Evaluator to produce a valid verdict; if the sprint's iteration limit has been reached, escalate instead. |

Allow at most **3 iterations per sprint**. If the third evaluation is not `PASS`, stop
the loop and escalate; do not silently continue or start the next sprint. The
orchestrator writes `.harness/output/escalation.md` with:

```markdown
# Harness Escalation

- Sprint: <sprint name>
- Iterations used: 3
- Blocking issue: <concise unresolved evaluator finding>
- Latest verdict: <CONDITIONAL PASS, FAIL, or missing/unrecognized>
- Required decision/action: <what a developer must resolve>
```

The blocking issue must come from the latest evaluator feedback (including the
unrecognized/missing-verdict case). Also invoke the Monitor with the escalation
outcome. Escalation requires developer intervention before this sprint can proceed.

## 5. Context scoping

Keep each invocation focused to prevent context-window degradation during long runs:

- Start each invocation with its agent prompt, the current request/spec/contract, and
  only the repository rules and source files relevant to that task.
- For a Generator retry, provide the latest evaluator feedback and the relevant
  changed files; do not replay earlier conversation or unrelated sprint history.
- For Evaluator and Monitor invocations, pass concise summaries and the specific
  artifacts they must check or record, rather than the full agent transcripts.
- Treat `spec.md`, `sprint-contract.md`, `generator-summary.md`, and
  `evaluator-feedback.md` as the persisted handoff between invocations. Re-read the
  current versions when needed; do not rely on unstored context.
- Preserve only stable, cross-sprint architecture rules as shared context. A new sprint
  starts with its own contract and relevant code context, not the previous sprint's
  full working context.

## 6. CI/CD relationship

The Evaluator's lint, type-check, and test-runner checks are an early feedback gate
inside the harness. They help catch issues before the work is handed off; they do not
replace, bypass, or weaken the repository's existing CI/CD pipeline gates. Once
accepted by the harness, the changes must still pass the normal CI checks (including
any additional integration, security, or deployment gates configured by the
repository). Existing pipeline configuration remains authoritative.
