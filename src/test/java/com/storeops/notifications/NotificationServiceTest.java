package com.storeops.notifications;

import com.storeops.notifications.dto.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    NotificationRepository repo;
    NotificationService service;

    @BeforeEach
    void setup() {
        repo = new InMemoryNotificationRepository();
        service = new NotificationService(repo);
    }

    @Test
    void getForUser_ok() {
        List<NotificationDto> list = service.getForUser("user-1");
        assertFalse(list.isEmpty());
    }

    @Test
    void getForUser_empty() {
        List<NotificationDto> list = service.getForUser("missing");
        assertTrue(list.isEmpty());
    }
}
