package com.justiceserve.complianceservice.controller;
import com.justiceserve.complianceservice.entity.AuditLog;
import com.justiceserve.complianceservice.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Slf4j @RestController @RequestMapping("/api/audit-logs") @RequiredArgsConstructor
@Tag(name = "Audit Logs")
public class AuditLogController {
    private final AuditLogRepository repo;

   @PostMapping("/internal")
    public ResponseEntity<Void> logInternal(@RequestParam Long userId,
                                            @RequestParam String action,
                                            @RequestParam String resource) {
        try {
            repo.save(AuditLog.builder().userId(userId).action(action).resource(resource).build());
            log.debug("AUDIT | userId={} | {} | {}", userId, action, resource);
        } catch (Exception e) { log.error("Audit log save failed: {}", e.getMessage()); }
        return ResponseEntity.ok().build();
    }

    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public ResponseEntity<List<AuditLog>> getAll() { return ResponseEntity.ok(repo.findAll()); }

    @GetMapping("/user/{userId}") @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public ResponseEntity<List<AuditLog>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(repo.findByUserIdOrderByTimestampDesc(userId));
    }
}