package com.justiceserve.judgmentservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
@FeignClient(name = "notification-service")
public interface NotificationFeignClient {
    @PostMapping("/api/notifications")
    void send(@RequestBody Map<String, Object> request);
}