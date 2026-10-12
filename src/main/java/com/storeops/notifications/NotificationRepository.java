package com.storeops.notifications;

import java.util.List;

public interface NotificationRepository {
    List<Notification> findByUserId(String userId);
}
