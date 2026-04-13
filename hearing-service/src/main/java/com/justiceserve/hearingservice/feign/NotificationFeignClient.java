package com.justiceserve.hearingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "notification-service", fallback = NotificationFeignClient.NotificationFeignFallback.class)
public interface NotificationFeignClient {
    @PostMapping("/api/notifications/internal")
    void send(@RequestBody Map<String, Object> payload);

    @org.springframework.stereotype.Component
    class NotificationFeignFallback implements NotificationFeignClient {
        @Override
        public void send(Map<String, Object> payload) {
        }
    }
}
