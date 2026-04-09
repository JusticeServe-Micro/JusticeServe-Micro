package com.justiceserve.citizenservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
/** Feign → compliance-service to write audit log entries. */
@FeignClient(name = "compliance-service")
public interface AuditFeignClient {
    @PostMapping("/api/audit-logs/internal")
    void log(@RequestParam("userId") Long userId, @RequestParam("action") String action, @RequestParam("resource") String resource);
}
