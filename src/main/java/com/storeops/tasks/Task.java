package com.storeops.tasks;

public class Task {

    private String id;
    private String title;
    private String storeId;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskCategory category;
    private String assigneeUserId;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public TaskPriority getPriority() {
		return priority;
	}
	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}
	public TaskCategory getCategory() {
		return category;
	}
	public void setCategory(TaskCategory category) {
		this.category = category;
	}
	public String getAssigneeUserId() {
		return assigneeUserId;
	}
	public void setAssigneeUserId(String assigneeUserId) {
		this.assigneeUserId = assigneeUserId;
	}


}
