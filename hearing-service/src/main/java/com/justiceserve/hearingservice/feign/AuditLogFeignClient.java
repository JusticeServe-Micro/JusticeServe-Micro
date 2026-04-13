package com.justiceserve.hearingservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "compliance-service")
public interface AuditLogFeignClient {
    @PostMapping("/api/audit-logs/internal")
    void log(@RequestParam Long userId, @RequestParam String action, @RequestParam String resource);
}