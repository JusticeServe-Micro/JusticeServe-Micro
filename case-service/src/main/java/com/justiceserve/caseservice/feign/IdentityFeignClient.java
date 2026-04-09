package com.justiceserve.caseservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service")
public interface IdentityFeignClient {
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Long id);

    record UserDto(Long userId, String name, String email, String role, String status) {}
}
