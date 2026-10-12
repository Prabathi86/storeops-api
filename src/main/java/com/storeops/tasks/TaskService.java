package com.storeops.tasks;

import com.storeops.common.EventBus;
import com.storeops.common.NotFoundError;
import com.storeops.common.ValidationError;
import com.storeops.tasks.dto.BulkTaskStatusUpdateRequest;
import com.storeops.tasks.dto.BulkTaskStatusUpdateResponse;
import com.storeops.tasks.dto.TaskDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository repo;
    private final EventBus bus;

    public TaskService(TaskRepository repo, EventBus bus) {
        this.repo = repo;
        this.bus = bus;
    }

    public TaskDto create(TaskDto dto) {
        if (dto.taskTitle == null || dto.taskTitle.isBlank()) {
            throw new ValidationError("TASK_TITLE_REQUIRED", "Task title is required");
        }
        Task t = toDomain(dto);
        t.setStatus(TaskStatus.TODO);
        Task saved = repo.save(t);
        bus.publish("tasks.created", saved);
        return toDto(saved);
    }

    public TaskDto get(String id) {
        Task t = repo.findById(id)
                .orElseThrow(() -> new NotFoundError("TASK_NOT_FOUND", "Task not found"));
        return toDto(t);
    }

    public List<TaskDto> list() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public TaskDto patch(String id, TaskDto patch) {
        Task t = repo.findById(id)
                .orElseThrow(() -> new NotFoundError("TASK_NOT_FOUND", "Task not found"));
        if (patch.taskStatus != null) t.setStatus(patch.taskStatus);
        if (patch.taskPriority != null) t.setPriority(patch.taskPriority);
        if (patch.taskCategory != null) t.setCategory(patch.taskCategory);
        if (patch.assigneeUserId != null) t.setAssigneeUserId(patch.assigneeUserId);
        Task saved = repo.save(t);
        bus.publish("tasks.updated", saved);
        return toDto(saved);
    }

    public BulkTaskStatusUpdateResponse bulkUpdateStatus(BulkTaskStatusUpdateRequest request) {
        if (request == null || request.updates == null || request.updates.isEmpty()) {
            throw new ValidationError("BULK_UPDATES_REQUIRED", "At least one update is required");
        }

        BulkTaskStatusUpdateResponse response = new BulkTaskStatusUpdateResponse();
        for (BulkTaskStatusUpdateRequest.Update update : request.updates) {
            String taskId = update == null ? null : update.taskId;
            if (taskId == null || taskId.isBlank()) {
                response.failed.add(new BulkTaskStatusUpdateResponse.FailedTask(
                        taskId, "TASK_ID_REQUIRED", "Task ID is required"));
                continue;
            }

            TaskStatus newStatus = parseBulkStatus(update.status);
            if (newStatus == null) {
                response.failed.add(new BulkTaskStatusUpdateResponse.FailedTask(
                        taskId, "TASK_STATUS_INVALID", "Status must be DONE or BLOCKED"));
                continue;
            }

            Task task = repo.findById(taskId).orElse(null);
            if (task == null) {
                response.failed.add(new BulkTaskStatusUpdateResponse.FailedTask(
                        taskId, "TASK_NOT_FOUND", "Task not found"));
                continue;
            }

            TaskStatus previousStatus = task.getStatus();
            task.setStatus(newStatus);
            Task saved = repo.save(task);
            bus.publish("tasks.status.bulk-updated", new TaskStatusAuditEvent(
                    saved.getId(), previousStatus, newStatus, Instant.now()));
            response.updated.add(new BulkTaskStatusUpdateResponse.UpdatedTask(
                    saved.getId(), newStatus.name()));
        }

        response.updatedCount = response.updated.size();
        response.failedCount = response.failed.size();
        return response;
    }

    public void delete(String id) {
        repo.deleteById(id);
        bus.publish("tasks.deleted", id);
    }

    private Task toDomain(TaskDto dto) {
        Task t = new Task();
        t.setId(dto.taskId);
        t.setTitle(dto.taskTitle);
        t.setStoreId(dto.storeId);
        t.setStatus(dto.taskStatus);
        t.setPriority(dto.taskPriority);
        t.setCategory(dto.taskCategory);
        t.setAssigneeUserId(dto.assigneeUserId);
        return t;
    }

    private TaskDto toDto(Task t) {
        TaskDto dto = new TaskDto();
        dto.taskId = t.getId();
        dto.taskTitle = t.getTitle();
        dto.storeId = t.getStoreId();
        dto.taskStatus = t.getStatus();
        dto.taskPriority = t.getPriority();
        dto.taskCategory = t.getCategory();
        dto.assigneeUserId = t.getAssigneeUserId();
        return dto;
    }

    private TaskStatus parseBulkStatus(String status) {
        if (TaskStatus.DONE.name().equals(status)) return TaskStatus.DONE;
        if (TaskStatus.BLOCKED.name().equals(status)) return TaskStatus.BLOCKED;
        return null;
    }
}
