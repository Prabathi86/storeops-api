package com.storeops.notifications;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {
    private final List<Notification> store = new ArrayList<>();

    public InMemoryNotificationRepository() {
        Notification n = new Notification();
        n.setId("notif-1");
        n.setUserId("user-1");
        n.setMessage("Inventory low in dairy");
        n.setChannel(NotificationChannel.IN_APP);
        n.setStatus(NotificationStatus.PENDING);
        n.setAlertType(AlertType.INVENTORY);
        store.add(n);
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : store) {
            if (userId.equals(n.getUserId())) result.add(n);
        }
        return result;
    }
}
