package com.justiceserve.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.justiceserve.notificationservice.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationRequest {
    @NotNull
    private Long userId;
    private Long entityId;
    @NotBlank
    private String message;
    @NotNull
    private Notification.NotificationCategory category;

    @JsonCreator
    public NotificationRequest(
            @JsonProperty("userId") Long userId,
            @JsonProperty("entityId") Long entityId,
            @JsonProperty("message") String message,
            @JsonProperty("category") Object category) {
        this.userId = userId;
        this.entityId = entityId;
        this.message = (String) message;
        // Accept both enum and String (Feign calls send string)
        if (category instanceof Notification.NotificationCategory) {
            this.category = (Notification.NotificationCategory) category;
        } else if (category instanceof String) {
            this.category = Notification.NotificationCategory.valueOf((String) category);
        }
    }
}
