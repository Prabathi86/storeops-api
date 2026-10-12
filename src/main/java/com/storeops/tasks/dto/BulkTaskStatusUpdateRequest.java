package com.storeops.tasks.dto;

import java.util.List;

public class BulkTaskStatusUpdateRequest {
    public List<Update> updates;

    public static class Update {
        public String taskId;
        public String status;
    }
}
