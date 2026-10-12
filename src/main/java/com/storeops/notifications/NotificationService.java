package com.storeops.notifications;

import com.storeops.notifications.dto.NotificationDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) { this.repo = repo; }

    public List<NotificationDto> getForUser(String userId) {
        return repo.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.notificationId = n.getId();
        dto.userId = n.getUserId();
        dto.message = n.getMessage();
        dto.channel = n.getChannel();
        dto.status = n.getStatus();
        dto.alertType = n.getAlertType();
        return dto;
    }
}
