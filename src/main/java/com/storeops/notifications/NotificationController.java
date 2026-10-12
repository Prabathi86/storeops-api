package com.storeops.notifications;

import com.storeops.notifications.dto.NotificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> list(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getForUser(userId));
    }
}
