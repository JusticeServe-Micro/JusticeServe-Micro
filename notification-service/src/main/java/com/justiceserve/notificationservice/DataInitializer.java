package com.justiceserve.notificationservice;

import com.justiceserve.notificationservice.entity.Notification;
import com.justiceserve.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final NotificationRepository notificationRepo;

    @Override
    public void run(String... args) {
        seedNotification(5L, 1L, "Your case has been scheduled for hearing.", Notification.NotificationCategory.CASE, Notification.NotificationStatus.UNREAD);
        seedNotification(12L, 2L, "Your submitted document has been approved.", Notification.NotificationCategory.HEARING, Notification.NotificationStatus.READ);
        seedNotification(13L, 1L, "Judgment for your case is now available.", Notification.NotificationCategory.JUDGMENT, Notification.NotificationStatus.UNREAD);
    }

    private void seedNotification(Long userId, Long entityId, String message, Notification.NotificationCategory category, Notification.NotificationStatus status) {
        Notification n = Notification.builder()
                .userId(userId)
                .entityId(entityId)
                .message(message)
                .category(category)
                .status(status)
                .build();
        notificationRepo.save(n);
        log.info("Seeded notification: {} for user {}", message, userId);
    }
}
