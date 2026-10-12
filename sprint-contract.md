# Sprint Contract — Shift Handover Bulk Update (Sprint 1)

## Objective
Implement bulk status update for tasks with audit logging and partial failure handling.

---

# Deliverables

## 1. Code
- Add `BulkStatusUpdateDto`
- Add `BulkStatusResultDto`
- Add audit entry model inside Task domain
- Extend `TaskRepository` with `saveAuditEntry(TaskAudit audit)`
- Add `TaskService.bulkStatusUpdate(dto)`
- Add `TaskController.bulkStatusUpdate(...)`
- Add event bus placeholder call (actual events in Sprint 2)

## 2. Tests
### Controller (MockMvc)
- test_bulkUpdate_success()
- test_bulkUpdate_partialFailure()
- test_bulkUpdate_invalidStatus()

### Service
- test_bulkUpdate_createsAuditEntries()
- test_bulkUpdate_partialFailure()
- test_bulkUpdate_updatesCorrectTasks()

---

# Acceptance Criteria Mapping

| AC | Description | Sprint Coverage |
|----|-------------|-----------------|
| AC1 | Bulk update succeeds | YES |
| AC2 | Partial failure | YES |
| AC3 | Status validation | YES |
| AC4 | Audit entry correctness | YES |
| AC5 | Module boundaries | YES |

---

# Constraints

- No cross-module repository imports
- No raw Error throws
- No business logic in controller
- No writes in reports module
- Event bus calls stubbed (real events in Sprint 2)

---

# Generator Instructions

Implement ONLY Sprint 1 deliverables.

Produce:
- Code changes
- Tests
- generator-summary.md with:
  - AC self-check table
  - Files changed
  - Known gaps
  - Next sprint suggestions

---

# Evaluator Instructions

Check:
- Lint
- Type safety
- Test pass/fail
- Module boundaries
- Error contract
- Layer separation

Produce evaluator-feedback.md with:
- Verdict (PASS / CONDITIONAL PASS / FAIL)
- File-level feedback
- Line-level feedback
- Required fixes

---

# Monitor Instructions

Record:
- iteration number
- verdict
- escalation flag
- token estimate
- quality trend notes

Write run-log.md under:
`.harness/reviews/<timestamp>/run-log.md`

