package com.justiceserve.citizenservice.controller;
import com.justiceserve.citizenservice.dto.*;
import com.justiceserve.citizenservice.entity.CitizenDocument;
import com.justiceserve.citizenservice.service.CitizenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * Module 2.2 — Citizen Registration & Profile Management
 */
@RestController @RequestMapping("/api/citizens") @RequiredArgsConstructor
@Tag(name = "2.2 Citizen Management")
public class CitizenController {
    private final CitizenService service;
    @PostMapping @PreAuthorize("hasAnyRole('CITIZEN','ADMIN','CLERK')") public ResponseEntity<CitizenResponse> create(@Valid @RequestBody CitizenRequest req) { return ResponseEntity.ok(service.createCitizen(req)); }
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','CLERK')") public ResponseEntity<List<CitizenResponse>> getAll() { return ResponseEntity.ok(service.getAllCitizens()); }
    @GetMapping("/{id}") public ResponseEntity<CitizenResponse> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getCitizenById(id)); }
    @GetMapping("/user/{userId}") public ResponseEntity<CitizenResponse> getByUserId(@PathVariable Long userId) { return ResponseEntity.ok(service.getCitizenByUserId(userId)); }
    @PutMapping("/{id}") public ResponseEntity<CitizenResponse> update(@PathVariable Long id, @Valid @RequestBody CitizenRequest req) { return ResponseEntity.ok(service.updateCitizen(id, req)); }
    @PostMapping("/{id}/documents") public ResponseEntity<DocumentResponse> addDoc(@PathVariable Long id, @Valid @RequestBody DocumentRequest req) { return ResponseEntity.ok(service.addDocument(id, req)); }
    @GetMapping("/{id}/documents") public ResponseEntity<List<DocumentResponse>> getDocs(@PathVariable Long id) { return ResponseEntity.ok(service.getDocuments(id)); }
    @PatchMapping("/{citizenId}/documents/{docId}/verify") @PreAuthorize("hasAnyRole('ADMIN','CLERK')")
    public ResponseEntity<DocumentResponse> verify(@PathVariable Long docId, @RequestParam CitizenDocument.VerificationStatus status) { return ResponseEntity.ok(service.verifyDocument(docId, status)); }
}
