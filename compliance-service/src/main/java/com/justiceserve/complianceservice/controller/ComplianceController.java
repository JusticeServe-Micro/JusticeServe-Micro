package com.justiceserve.complianceservice.controller;
import com.justiceserve.complianceservice.entity.*;
import com.justiceserve.complianceservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class ComplianceController {
    private final ComplianceRecordRepository complianceRepo;
    private final AuditRepository auditRepo;

    @PostMapping("/compliance") @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE','AUDITOR')")
    public ResponseEntity<ComplianceRecord> createRecord(@RequestBody ComplianceRecord req) { return ResponseEntity.ok(complianceRepo.save(req)); }
    @GetMapping("/compliance") public ResponseEntity<List<ComplianceRecord>> getAllRecords() { return ResponseEntity.ok(complianceRepo.findAll()); }
    @PatchMapping("/compliance/{id}") @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE')")
    public ResponseEntity<ComplianceRecord> updateRecord(@PathVariable Long id, @RequestBody ComplianceRecord req) {
        ComplianceRecord r = complianceRepo.findById(id).orElseThrow();
        r.setResult(req.getResult()); r.setNotes(req.getNotes());
        return ResponseEntity.ok(complianceRepo.save(r));
    }

    @PostMapping("/audits") @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE','AUDITOR')")
    public ResponseEntity<Audit> createAudit(@RequestBody Audit req) { return ResponseEntity.ok(auditRepo.save(req)); }
    @GetMapping("/audits") public ResponseEntity<List<Audit>> getAllAudits() { return ResponseEntity.ok(auditRepo.findAll()); }
    @PatchMapping("/audits/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE','AUDITOR')")
    public ResponseEntity<Audit> updateAuditStatus(@PathVariable Long id, @RequestParam Audit.AuditStatus status) {
        Audit a = auditRepo.findById(id).orElseThrow(); a.setStatus(status); return ResponseEntity.ok(auditRepo.save(a));
    }
}