package com.justiceserve.hearingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "case-service", fallback = CaseFeignClient.CaseFeignFallback.class)
public interface CaseFeignClient {
    @PatchMapping("/api/cases/{id}/status")
    void updateStatus(@PathVariable Long id, @RequestParam String status);

    @org.springframework.stereotype.Component
    class CaseFeignFallback implements CaseFeignClient {
        @Override
        public void updateStatus(Long id, String status) {
            // Log and continue — case status will need manual update
        }
    }
}
