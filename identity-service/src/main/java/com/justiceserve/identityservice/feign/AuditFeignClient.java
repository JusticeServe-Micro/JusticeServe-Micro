package com.justiceserve.identityservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "compliance-service")
public interface AuditFeignClient {
    @PostMapping("/api/audit-logs/internal")
    void log(@RequestParam("userId") Long userId,
             @RequestParam("action") String action,
             @RequestParam("resource") String resource);
}
