package com.storeops.tasks;

import com.storeops.common.EventBus;
import com.storeops.common.ValidationError;
import com.storeops.tasks.dto.BulkTaskStatusUpdateRequest;
import com.storeops.tasks.dto.BulkTaskStatusUpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BulkTaskStatusServiceTest {
    private TaskRepository repo;
    private TaskService service;
    private List<PublishedEvent> events;

    @BeforeEach
    void setup() {
        repo = new InMemoryTaskRepository();
        events = new ArrayList<>();
        EventBus bus = (topic, payload) -> events.add(new PublishedEvent(topic, payload));
        service = new TaskService(repo, bus);

        saveTask("task-1", TaskStatus.IN_PROGRESS);
        saveTask("task-2", TaskStatus.TODO);
    }

    @Test
    void bulkUpdateStatus_updatesAndAuditsEachValidTask() {
        BulkTaskStatusUpdateResponse response = service.bulkUpdateStatus(request(
                update("task-1", "DONE"),
                update("task-2", "BLOCKED")));

        assertEquals(2, response.updatedCount);
        assertEquals(0, response.failedCount);
        assertEquals("DONE", response.updated.get(0).status);
        assertEquals("BLOCKED", response.updated.get(1).status);
        assertEquals(TaskStatus.DONE, repo.findById("task-1").orElseThrow().getStatus());
        assertEquals(TaskStatus.BLOCKED, repo.findById("task-2").orElseThrow().getStatus());
        assertEquals(2, events.size());

        PublishedEvent first = events.get(0);
        assertEquals("tasks.status.bulk-updated", first.topic());
        TaskStatusAuditEvent audit = (TaskStatusAuditEvent) first.payload();
        assertEquals("task-1", audit.taskId());
        assertEquals(TaskStatus.IN_PROGRESS, audit.oldStatus());
        assertEquals(TaskStatus.DONE, audit.newStatus());
        assertNotNull(audit.timestamp());
    }

    @Test
    void bulkUpdateStatus_reportsExpectedItemFailuresAndContinues() {
        BulkTaskStatusUpdateResponse response = service.bulkUpdateStatus(request(
                update("missing", "DONE"),
                update("task-1", "IN_PROGRESS"),
                update(" ", "BLOCKED"),
                update("task-1", null),
                null,
                update("task-2", "BLOCKED")));

        assertEquals(1, response.updatedCount);
        assertEquals(5, response.failedCount);
        assertEquals("task-2", response.updated.get(0).taskId);
        assertEquals(TaskStatus.BLOCKED, repo.findById("task-2").orElseThrow().getStatus());
        assertEquals(List.of(
                "TASK_NOT_FOUND",
                "TASK_STATUS_INVALID",
                "TASK_ID_REQUIRED",
                "TASK_STATUS_INVALID",
                "TASK_ID_REQUIRED"),
                response.failed.stream().map(failure -> failure.code).toList());
        assertEquals(1, events.size());
        assertEquals("task-2",
                ((TaskStatusAuditEvent) events.get(0).payload()).taskId());
    }

    @Test
    void bulkUpdateStatus_rejectsMissingOrEmptyUpdates() {
        assertThrows(ValidationError.class, () -> service.bulkUpdateStatus(null));
        assertThrows(ValidationError.class,
                () -> service.bulkUpdateStatus(new BulkTaskStatusUpdateRequest()));

        BulkTaskStatusUpdateRequest emptyRequest = new BulkTaskStatusUpdateRequest();
        emptyRequest.updates = List.of();
        assertThrows(ValidationError.class, () -> service.bulkUpdateStatus(emptyRequest));
        assertTrue(events.isEmpty());
    }

    @Test
    void bulkUpdateStatus_doesNotSwallowRepositoryOrEventBusFailures() {
        Task task = new Task();
        task.setId("task-save-failure");
        task.setStatus(TaskStatus.TODO);
        TaskRepository failingRepository = new TaskRepository() {
            @Override
            public Task save(Task task) {
                throw new IllegalStateException("repository unavailable");
            }

            @Override
            public Optional<Task> findById(String id) {
                return Optional.of(task);
            }

            @Override
            public List<Task> findAll() {
                return List.of(task);
            }

            @Override
            public void deleteById(String id) {}
        };

        TaskService repositoryFailureService = new TaskService(failingRepository, (topic, payload) -> {
        });
        assertThrows(IllegalStateException.class,
                () -> repositoryFailureService.bulkUpdateStatus(
                        request(update("task-save-failure", "DONE"))));

        TaskService eventBusFailureService = new TaskService(repo, (topic, payload) -> {
            throw new IllegalStateException("event bus unavailable");
        });
        assertThrows(IllegalStateException.class,
                () -> eventBusFailureService.bulkUpdateStatus(
                        request(update("task-1", "DONE"))));
    }

    private void saveTask(String taskId, TaskStatus status) {
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(status);
        repo.save(task);
    }

    private BulkTaskStatusUpdateRequest request(BulkTaskStatusUpdateRequest.Update... updates) {
        BulkTaskStatusUpdateRequest request = new BulkTaskStatusUpdateRequest();
        request.updates = Arrays.asList(updates);
        return request;
    }

    private BulkTaskStatusUpdateRequest.Update update(String taskId, String status) {
        BulkTaskStatusUpdateRequest.Update update = new BulkTaskStatusUpdateRequest.Update();
        update.taskId = taskId;
        update.status = status;
        return update;
    }

    private record PublishedEvent(String topic, Object payload) {
    }
}
