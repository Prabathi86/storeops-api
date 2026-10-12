# StoreOps Feature Spec
## Feature: Shift Handover Bulk Update
## Endpoint: PATCH /api/tasks/bulk-status

---

# 1. Problem Statement

During shift handover, outgoing staff must update the status of multiple operational tasks
simultaneously. Today, StoreOps only supports single-task PATCH operations. This creates
friction, delays, and inconsistent audit trails.

We need a bulk-status update endpoint that:

- Accepts multiple task IDs
- Applies a new status (DONE or BLOCKED)
- Records an audit entry per updated task
- Handles partial failures gracefully
- Emits an event bus entry per updated task
- Returns a structured result indicating successes and failures

---

# 2. Requirements

## 2.1 API Contract

### Method
