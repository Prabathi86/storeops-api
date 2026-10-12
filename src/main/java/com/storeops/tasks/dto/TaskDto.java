package com.storeops.tasks.dto;

import com.storeops.tasks.TaskCategory;
import com.storeops.tasks.TaskPriority;
import com.storeops.tasks.TaskStatus;

public class TaskDto {
    public String taskId;
    public String taskTitle;
    public String storeId;
    public TaskStatus taskStatus;
    public TaskPriority taskPriority;
    public TaskCategory taskCategory;
    public String assigneeUserId;
}
