# Sprint Contract: Shift Handover Bulk Status Update

## Scope

Implement one sprint for `PATCH /api/activities/bulk-status` using the existing task
domain/service/repository patterns. Support per-item partial failure and one
`tasks.status.bulk-updated` EventBus audit event per successfully saved task. Keep
durable audit storage and unrelated activity/programme endpoints out of scope.

## Implementation requirements

- Add request/response DTOs and controller/service support as appropriate to existing
  project conventions.
- Accept an `updates` array with `taskId` and `status`; only `DONE` and `BLOCKED` are
  valid statuses.
- Reject a missing or empty request-level updates array using the established
  validation error contract.
- Process entries independently in request order. Return per-item successes and
  failures with accurate counts; keep each result in the corresponding array's
  request-relative order.
- Use item failure codes `TASK_ID_REQUIRED`, `TASK_STATUS_INVALID`, and
  `TASK_NOT_FOUND` as applicable. Continue after expected per-item failures.
- Save each valid update before publishing exactly one audit event on
  `tasks.status.bulk-updated`; include task ID, prior status, new status, and an
  ISO-8601 timestamp. Do not audit failed items.
- Do not broadly catch or hide unexpected repository/EventBus failures.
- Preserve existing `/api/tasks` behavior.

## Acceptance criteria

- [ ] Multiple valid tasks update in one request and each produces one audit event.
- [ ] A missing task in a mixed request is reported as a failure without blocking valid
      updates.
- [ ] Blank task IDs and missing/unsupported statuses fail per item without blocking
      independent valid updates.
- [ ] Missing or empty `updates` is rejected as a request-level validation error.
- [ ] Unexpected infrastructure failures are not returned as successful item results.
- [ ] Existing task endpoints remain behaviorally unchanged.

## Verification expected

- Add focused service tests for validation, success, mixed success/failure, stable
  counts/order, and per-success EventBus audit payloads.
- Add controller tests for the route, HTTP status, JSON request/response shape, and
  request-level validation.
- Run the focused task tests and the project's available test/build checks. Report
  unavailable toolchains or checks explicitly rather than claiming they passed.

## Approval gate

No implementation begins until the developer types exactly `APPROVED`.
