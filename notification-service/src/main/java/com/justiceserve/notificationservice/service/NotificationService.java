package com.justiceserve.notificationservice.service;

import com.justiceserve.notificationservice.dto.NotificationRequest;
import com.justiceserve.notificationservice.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse send(NotificationRequest req);

    List<NotificationResponse> getByUser(Long userId);

    NotificationResponse markRead(Long id);

    long countUnread(Long userId);

    void markAllRead(Long userId);
}