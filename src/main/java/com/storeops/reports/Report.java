package com.storeops.reports;

public class Report {
    private String id;
    private ReportType type;
    private ReportStatus status;
    private String targetId;
    private String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ReportType getType() {
		return type;
	}
	public void setType(ReportType type) {
		this.type = type;
	}
	public ReportStatus getStatus() {
		return status;
	}
	public void setStatus(ReportStatus status) {
		this.status = status;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

    
}
