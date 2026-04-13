package com.justiceserve.caseservice.controller;

import com.justiceserve.caseservice.dto.*;
import com.justiceserve.caseservice.entity.Case;
import com.justiceserve.caseservice.entity.CaseDocument;
import com.justiceserve.caseservice.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('CITIZEN','LAWYER','CLERK','ADMIN')")
    public ResponseEntity<CaseResponse> fileCase(@Valid @RequestBody CaseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.fileCase(req));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLERK','JUDGE','COMPLIANCE','AUDITOR')")
    public ResponseEntity<List<CaseResponse>> getAll() { return ResponseEntity.ok(service.getAllCases()); }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getCaseById(id)); }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<CaseResponse>> getByCitizen(@PathVariable Long citizenId) { return ResponseEntity.ok(service.getCasesByCitizen(citizenId)); }

    @GetMapping("/lawyer/{lawyerId}")
    public ResponseEntity<List<CaseResponse>> getByLawyer(@PathVariable Long lawyerId) { return ResponseEntity.ok(service.getCasesByLawyer(lawyerId)); }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CaseResponse>> getByStatus(@PathVariable Case.CaseStatus status) { return ResponseEntity.ok(service.getCasesByStatus(status)); }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK','JUDGE')")
    public ResponseEntity<CaseResponse> updateStatus(@PathVariable Long id, @RequestParam Case.CaseStatus status) {
        return ResponseEntity.ok(service.updateCaseStatus(id, status));
    }

    @PatchMapping("/{id}/assign-lawyer")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK')")
    public ResponseEntity<CaseResponse> assignLawyer(@PathVariable Long id, @RequestParam Long lawyerId) {
        return ResponseEntity.ok(service.assignLawyer(id, lawyerId));
    }

    @PatchMapping("/{id}/remove-lawyer")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK')")
    public ResponseEntity<CaseResponse> removeLawyer(@PathVariable Long id) { return ResponseEntity.ok(service.removeLawyer(id)); }

    @PatchMapping("/{id}/assign-judge")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK')")
    public ResponseEntity<CaseResponse> assignJudge(@PathVariable Long id, @RequestParam Long judgeId) {
        return ResponseEntity.ok(service.assignJudge(id, judgeId));
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<DocumentResponse> addDocument(@PathVariable Long id, @Valid @RequestBody DocumentRequest req) {
        return ResponseEntity.ok(service.addDocument(id, req));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<DocumentResponse>> getDocuments(@PathVariable Long id) { return ResponseEntity.ok(service.getDocuments(id)); }

    @PatchMapping("/{caseId}/documents/{docId}/verify")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK')")
    public ResponseEntity<DocumentResponse> verifyDocument(@PathVariable Long docId, @RequestParam CaseDocument.VerificationStatus status) {
        return ResponseEntity.ok(service.verifyDocument(docId, status));
    }
}
