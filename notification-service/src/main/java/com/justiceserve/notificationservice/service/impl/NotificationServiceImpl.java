package com.justiceserve.notificationservice.service.impl;
import com.justiceserve.notificationservice.dto.*;
import com.justiceserve.notificationservice.entity.Notification;
import com.justiceserve.notificationservice.exception.ResourceNotFoundException;
import com.justiceserve.notificationservice.repository.NotificationRepository;
import com.justiceserve.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repo;
    @Override public NotificationResponse send(NotificationRequest req) {
        Notification n = Notification.builder().userId(req.getUserId()).entityId(req.getEntityId())
                .message(req.getMessage()).category(req.getCategory()).build();
        return NotificationResponse.from(repo.save(n));
    }
    @Override public List<NotificationResponse> getByUser(Long userId) {
        return repo.findByUserIdOrderByCreatedDateDesc(userId).stream().map(NotificationResponse::from).toList();
    }
    @Override public NotificationResponse markRead(Long id) {
        Notification n = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        n.setStatus(Notification.NotificationStatus.READ);
        return NotificationResponse.from(repo.save(n));
    }
    @Override public long countUnread(Long userId) {
        return repo.countByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD);
    }
    @Override public void markAllRead(Long userId) {
        var list = repo.findByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD);
        list.forEach(n -> n.setStatus(Notification.NotificationStatus.READ));
        repo.saveAll(list);
    }
}