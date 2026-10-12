package com.storeops.notifications;

public class Notification {
    private String id;
    private String userId;
    private String message;
	private NotificationChannel channel;
    private NotificationStatus status;
    private AlertType alertType;
    
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public NotificationChannel getChannel() {
		return channel;
	}
	public void setChannel(NotificationChannel channel) {
		this.channel = channel;
	}
	public NotificationStatus getStatus() {
		return status;
	}
	public void setStatus(NotificationStatus status) {
		this.status = status;
	}
	public AlertType getAlertType() {
		return alertType;
	}
	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}


}
