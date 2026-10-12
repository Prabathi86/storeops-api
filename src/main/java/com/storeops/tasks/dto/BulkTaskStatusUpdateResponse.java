package com.storeops.tasks.dto;

import java.util.ArrayList;
import java.util.List;

public class BulkTaskStatusUpdateResponse {
    public List<UpdatedTask> updated = new ArrayList<>();
    public List<FailedTask> failed = new ArrayList<>();
    public int updatedCount;
    public int failedCount;

    public static class UpdatedTask {
        public String taskId;
        public String status;

        public UpdatedTask(String taskId, String status) {
            this.taskId = taskId;
            this.status = status;
        }
    }

    public static class FailedTask {
        public String taskId;
        public String code;
        public String message;

        public FailedTask(String taskId, String code, String message) {
            this.taskId = taskId;
            this.code = code;
            this.message = message;
        }
    }
}
