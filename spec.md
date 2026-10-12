# Feature Spec: Shift Handover Bulk Status Update

## Goal

Allow outgoing shift staff to mark multiple operational activities DONE or BLOCKED in
one request, while reporting item-level failures and recording an audit event for every
successfully updated task.

## Repository context and constraints

- Operational activities are currently represented by the `Task` domain and handled by
  `TaskService` and `TaskRepository`.
- Add the requested route `PATCH /api/activities/bulk-status`; preserve existing
  `/api/tasks` routes and behavior.
- Reuse the existing `EventBus` for audit events. In this repository its implementation
  logs published events; durable audit storage and a new audit-query API are out of
  scope.
- Follow the existing `AppError`, `ValidationError`, and `GlobalExceptionHandler`
  error contract for request-level errors. Do not mask unexpected infrastructure
  failures as successful item results.

## Request

```json
{
  "updates": [
    { "taskId": "task-1", "status": "DONE" },
    { "taskId": "task-2", "status": "BLOCKED" }
  ]
}
```

`updates` must be present and non-empty. Each item requires a non-blank `taskId` and a
status of exactly `DONE` or `BLOCKED`. Status is item-level input: an unsupported or
missing status must fail that item without preventing independent valid items from
being processed.

## Partial-failure behavior

- Process items independently and in request order.
- A missing task ID, blank task ID, unsupported/missing status, or task not found is an
  item failure. Continue processing subsequent items.
- Save valid updates and report them as successes. Do not report a failed update as
  successful or publish its audit event.
- Unexpected repository or event-bus failures must use the established error path;
  do not catch broadly or silently convert infrastructure failures to item failures.
- A structurally invalid request (missing/empty `updates` collection or malformed
  JSON) is a request-level error and does not begin item processing.

For a structurally valid request, return HTTP 200 with a result for every input item,
including when some or all items fail:

```json
{
  "updated": [
    { "taskId": "task-1", "status": "DONE" }
  ],
  "failed": [
    {
      "taskId": "task-404",
      "code": "TASK_NOT_FOUND",
      "message": "Task not found"
    }
  ],
  "updatedCount": 1,
  "failedCount": 1
}
```

The `updated` and `failed` arrays retain the relative request order of their entries.
Use stable error codes for item failures, including `TASK_ID_REQUIRED`,
`TASK_STATUS_INVALID`, and `TASK_NOT_FOUND`.

## Audit event

After each valid task update is saved, publish exactly one event through the existing
`EventBus` on topic `tasks.status.bulk-updated`. Include the task ID, previous status,
new status, and an ISO-8601 event timestamp in the payload. Publish no audit event for
an item that fails validation or cannot be found. Audit events are emitted per task,
not as a single batch event.

## Acceptance criteria

1. GIVEN a valid request containing multiple existing tasks with statuses DONE or
   BLOCKED, WHEN the endpoint is called, THEN every task is updated, the response
   lists each updated task with its requested status, and one audit event is published
   for each updated task.
2. GIVEN a request containing both a valid item and an item whose task ID does not
   exist, WHEN the endpoint is called, THEN the valid task is updated and audited,
   the missing task appears in `failed` with `TASK_NOT_FOUND`, and the response
   reports accurate counts.
3. GIVEN an item with a blank task ID or unsupported/missing status alongside a valid
   item, WHEN the endpoint is called, THEN that item is reported as failed, valid
   items are still processed, and only successfully updated tasks are audited.
4. GIVEN a missing or empty `updates` collection, WHEN the endpoint is called, THEN
   the request is rejected using the existing request-level validation error contract.
5. GIVEN an unexpected repository or event-bus failure, WHEN an item is processed,
   THEN the failure is not swallowed or represented as a successful result.
6. GIVEN existing task endpoints and service behavior, WHEN bulk status support is
   added, THEN existing `/api/tasks` endpoints and their current behavior remain
   unchanged.
