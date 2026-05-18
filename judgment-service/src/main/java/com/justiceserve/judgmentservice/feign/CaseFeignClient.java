package com.justiceserve.judgmentservice.feign;

import com.justiceserve.judgmentservice.dto.CaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "case-service", fallback = CaseFeignClient.CaseFeignFallback.class)
public interface CaseFeignClient {
    @PatchMapping("/api/cases/{id}/status")
    void updateStatus(@PathVariable Long id, @RequestParam String status);

    @GetMapping("/api/cases/{id}")
    public ResponseEntity<CaseResponse> getById(@PathVariable Long id);


    @org.springframework.stereotype.Component
    class CaseFeignFallback implements CaseFeignClient {
        @Override
        public void updateStatus(Long id, String status) {
        }

        @Override
        public ResponseEntity<CaseResponse> getById(Long id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CaseResponse());
        }
    }
}
