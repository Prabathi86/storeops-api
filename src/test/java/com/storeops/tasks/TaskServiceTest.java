package com.storeops.tasks;

import com.storeops.common.EventBus;
import com.storeops.common.NotFoundError;
import com.storeops.common.ValidationError;
import com.storeops.tasks.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    TaskRepository repo;
    EventBus bus;
    TaskService service;

    @BeforeEach
    void setup() {
        repo = new InMemoryTaskRepository();
        bus = (topic, payload) -> {};
        service = new TaskService(repo, bus);

        Task t = new Task();
        t.setId("task-1");
        t.setTitle("Restock dairy");
        t.setStatus(TaskStatus.IN_PROGRESS);
        repo.save(t);
    }

    @Test
    void getTask_ok() {
        TaskDto dto = service.get("task-1");
        assertEquals("task-1", dto.taskId);
    }

    @Test
    void patchTask_updatesStatus() {
        TaskDto patch = new TaskDto();
        patch.taskStatus = TaskStatus.DONE;

        TaskDto updated = service.patch("task-1", patch);
        assertEquals(TaskStatus.DONE, updated.taskStatus);
    }

    @Test
    void getTask_notFound() {
        assertThrows(NotFoundError.class, () -> service.get("missing"));
    }
}
