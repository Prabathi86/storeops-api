package com.storeops.notifications.dto;

import com.storeops.notifications.AlertType;
import com.storeops.notifications.NotificationChannel;
import com.storeops.notifications.NotificationStatus;

public class NotificationDto {
    public String notificationId;
    public String userId;
    public String message;
    public NotificationChannel channel;
    public NotificationStatus status;
    public AlertType alertType;
}
