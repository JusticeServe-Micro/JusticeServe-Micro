package com.justiceserve.caseservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citizen-service")
public interface CitizenFeignClient {
    @GetMapping("/api/citizens/{id}")
    CitizenDto getCitizenById(@PathVariable Long id);

    record CitizenDto(Long citizenId, Long userId, String name, String email, String contactInfo, String status) {}
}
