package com.storeops.activities;

import com.storeops.tasks.Task;
import com.storeops.tasks.TaskRepository;
import com.storeops.tasks.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void bulkUpdateStatus_returnsPerItemResult() throws Exception {
        String taskId = UUID.randomUUID().toString();
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        mockMvc.perform(patch("/api/activities/bulk-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"updates":[{"taskId":"%s","status":"DONE"}]}
                                """.formatted(taskId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(0))
                .andExpect(jsonPath("$.updated[0].taskId").value(taskId))
                .andExpect(jsonPath("$.updated[0].status").value("DONE"));
    }

    @Test
    void bulkUpdateStatus_rejectsEmptyUpdates() throws Exception {
        mockMvc.perform(patch("/api/activities/bulk-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"updates\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BULK_UPDATES_REQUIRED"));
    }
}
