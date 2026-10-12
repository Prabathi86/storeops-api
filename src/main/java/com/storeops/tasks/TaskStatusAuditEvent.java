package com.storeops.tasks;

import java.time.Instant;

public record TaskStatusAuditEvent(
        String taskId,
        TaskStatus oldStatus,
        TaskStatus newStatus,
        Instant timestamp,
        String changedBy,
        String metadata
) {

    // Constructor matching your usage
    public TaskStatusAuditEvent(String taskId, TaskStatus oldStatus, TaskStatus newStatus, Instant timestamp) {
        this(taskId, oldStatus, newStatus, timestamp, "system", null);
    }

    // Optional minimal constructor
    public TaskStatusAuditEvent(String taskId, TaskStatus oldStatus, TaskStatus newStatus) {
        this(taskId, oldStatus, newStatus, Instant.now(), "system", null);
    }
}
