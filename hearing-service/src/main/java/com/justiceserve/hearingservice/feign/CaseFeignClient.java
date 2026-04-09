package com.justiceserve.hearingservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "case-service")
public interface CaseFeignClient {
    @PatchMapping("/api/cases/{id}/status")
    void updateStatus(@PathVariable Long id, @RequestParam String status);
}