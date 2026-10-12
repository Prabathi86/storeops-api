package com.storeops.activities;

import com.storeops.tasks.TaskService;
import com.storeops.tasks.dto.BulkTaskStatusUpdateRequest;
import com.storeops.tasks.dto.BulkTaskStatusUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final TaskService taskService;

    public ActivityController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PatchMapping("/bulk-status")
    public ResponseEntity<BulkTaskStatusUpdateResponse> bulkUpdateStatus(
            @RequestBody BulkTaskStatusUpdateRequest request) {
        return ResponseEntity.ok(taskService.bulkUpdateStatus(request));
    }
}
