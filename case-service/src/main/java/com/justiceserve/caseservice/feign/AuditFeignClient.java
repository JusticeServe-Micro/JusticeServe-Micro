package com.justiceserve.caseservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign → compliance-service: write audit log entries.
 * Circuit breaker: if compliance-service is down, audit silently fails
 * (non-critical path — main operation must not fail due to audit issues).
 * FeignClientConfig forwards JWT automatically.
 */
@FeignClient(name = "compliance-service", fallback = AuditFeignClient.AuditFeignFallback.class)
public interface AuditFeignClient {
    @PostMapping("/api/audit-logs/internal")
    void log(@RequestParam("userId") Long userId,
             @RequestParam("action") String action,
             @RequestParam("resource") String resource);

    @org.springframework.stereotype.Component
    class AuditFeignFallback implements AuditFeignClient {
        @Override
        public void log(Long userId, String action, String resource) {
            // Silently ignore — audit failure must not block main operation
        }
    }
}
