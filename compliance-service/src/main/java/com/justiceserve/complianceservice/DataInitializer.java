package com.justiceserve.complianceservice;

import com.justiceserve.complianceservice.entity.Audit;
import com.justiceserve.complianceservice.entity.AuditLog;
import com.justiceserve.complianceservice.entity.ComplianceRecord;
import com.justiceserve.complianceservice.repository.AuditLogRepository;
import com.justiceserve.complianceservice.repository.AuditRepository;
import com.justiceserve.complianceservice.repository.ComplianceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuditRepository auditRepo;
    private final AuditLogRepository auditLogRepo;
    private final ComplianceRecordRepository complianceRepo;

    @Override
    public void run(String... args) {
        // Seed audits
        Audit audit1 = seedAudit(6L, "Case Filing Audit", "Findings on case filing process", Audit.AuditStatus.OPEN);
        Audit audit2 = seedAudit(11L, "Document Verification Audit", "Findings on document verification", Audit.AuditStatus.REVIEW);

        // Seed audit logs
        seedAuditLog(6L, "Started audit process", "Audit resource");
        seedAuditLog(11L, "Completed verification", "Document resource");

        // Seed compliance records
        seedComplianceRecord(1L, ComplianceRecord.ComplianceType.CASE, ComplianceRecord.ComplianceResult.COMPLIANT, "Case 1 compliant");
        seedComplianceRecord(2L, ComplianceRecord.ComplianceType.HEARING, ComplianceRecord.ComplianceResult.NON_COMPLIANT, "Hearing 2 needs review");
    }

    private Audit seedAudit(Long officerId, String scope, String findings, Audit.AuditStatus status) {
        Audit a = Audit.builder()
                .officerId(officerId)
                .scope(scope)
                .findings(findings)
                .status(status)
                .build();
        auditRepo.save(a);
        log.info("Seeded audit: {}", scope);
        return a;
    }

    private void seedAuditLog(Long userId, String action, String resource) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .resource(resource)
                .build();
        auditLogRepo.save(log);
        log.info("Seeded audit log: {} for {}", action, resource);
    }

    private void seedComplianceRecord(Long entityId, ComplianceRecord.ComplianceType type, ComplianceRecord.ComplianceResult result, String notes) {
        ComplianceRecord rec = ComplianceRecord.builder()
                .entityId(entityId)
                .type(type)
                .result(result)
                .notes(notes)
                .build();
        complianceRepo.save(rec);
        log.info("Seeded compliance record: {} for entity {}", notes, entityId);
    }
}
