# Feature Implementation Output: Shift Handover Bulk Status Update

## Feature

Add `PATCH /api/activities/bulk-status` so outgoing shift staff can update multiple
operational activities to `DONE` or `BLOCKED` in one request, with per-item failure
handling and an audit event for every successfully updated task.

## Request example

```http
PATCH /api/activities/bulk-status
Content-Type: application/json
```

```json
{
  "updates": [
    { "taskId": "task-1", "status": "DONE" },
    { "taskId": "task-2", "status": "BLOCKED" }
  ]
}
```

## Response example

The endpoint returns HTTP 200 for a structurally valid batch. Each entry is reported
as updated or failed:

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

Supported per-item failures include `TASK_ID_REQUIRED`, `TASK_STATUS_INVALID`, and
`TASK_NOT_FOUND`. Missing or empty `updates` is rejected as a request-level validation
error. Other valid entries continue to be processed when one item fails.

## Implementation

- Added the `/api/activities/bulk-status` controller route and request/response DTOs.
- Reused the existing task repository and service.
- Limited accepted statuses to `DONE` and `BLOCKED`.
- Published one `tasks.status.bulk-updated` EventBus event after each successfully
  saved update. Each event includes the task ID, prior status, new status, and an
  ISO-8601 timestamp.
- Left unexpected repository and EventBus failures uncaught so they are not
  represented as successful item results.
- Added focused service and controller test sources.

The existing EventBus logs events; durable audit storage is not part of this feature.

## Files added or changed

- `src/main/java/com/storeops/activities/ActivityController.java`
- `src/main/java/com/storeops/tasks/TaskService.java`
- `src/main/java/com/storeops/tasks/TaskStatusAuditEvent.java`
- `src/main/java/com/storeops/tasks/dto/BulkTaskStatusUpdateRequest.java`
- `src/main/java/com/storeops/tasks/dto/BulkTaskStatusUpdateResponse.java`
- `src/test/java/com/storeops/activities/ActivityControllerTest.java`
- `src/test/java/com/storeops/tasks/BulkTaskStatusServiceTest.java`

## Validation

- Changed production and test sources compiled with Java 17 source/target compatibility.
- Three focused service test methods passed.
- The controller response and route mapping were checked directly.
- Workspace diagnostics found no Java errors, and `git diff --check` passed.
- The full Maven test suite was not run because Maven is unavailable in the environment.
  The workspace test runner did not discover the Java tests. Run the full suite and
  normal CI/CD gates in a Maven-enabled environment before merging.

## Harness result

Evaluator verdict: **PASS**. No follow-up sprint was required. The harness checks do
not replace the repository's existing CI/CD gates.
