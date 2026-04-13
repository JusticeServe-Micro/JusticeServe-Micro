package com.justiceserve.citizenservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*; import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationFeignClient {
    @PostMapping("/api/notifications/internal")
    void send(@RequestBody Map<String, Object> payload);
}
