package com.justiceserve.caseservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citizen-service", fallback = CitizenFeignClient.CitizenFeignFallback.class)
public interface CitizenFeignClient {
    @GetMapping("/api/citizens/{id}")
    CitizenDto getCitizenById(@PathVariable Long id);

    @GetMapping("/api/citizens/user/{userId}")
    CitizenDto getCitizenByUserId(@PathVariable Long userId);

    record CitizenDto(Long citizenId, Long userId, String name, String email, String contactInfo, String status) {}

    @org.springframework.stereotype.Component
    class CitizenFeignFallback implements CitizenFeignClient {
        @Override public CitizenDto getCitizenById(Long id) { return null; }
        @Override public CitizenDto getCitizenByUserId(Long userId) { return null; }
    }
}
