package com.justiceserve.notificationservice.dto;

import com.justiceserve.notificationservice.entity.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long notificationId;
    private Long userId;
    private Long entityId;
    private String message;
    private Notification.NotificationCategory category;
    private Notification.NotificationStatus status;
    private LocalDateTime createdDate;

    public static NotificationResponse from(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.notificationId = n.getNotificationId();
        r.userId = n.getUserId();
        r.entityId = n.getEntityId();
        r.message = n.getMessage();
        r.category = n.getCategory();
        r.status = n.getStatus();
        r.createdDate = n.getCreatedDate();
        return r;
    }
}