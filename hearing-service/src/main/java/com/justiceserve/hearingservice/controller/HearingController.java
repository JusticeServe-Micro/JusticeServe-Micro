package com.justiceserve.hearingservice.controller;

import com.justiceserve.hearingservice.dto.*;
import com.justiceserve.hearingservice.entity.Hearing;
import com.justiceserve.hearingservice.service.HearingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hearings")
@RequiredArgsConstructor
public class HearingController {

    private final HearingService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLERK','JUDGE')")
    public ResponseEntity<HearingResponse> schedule(@Valid @RequestBody HearingRequest req) {
        return ResponseEntity.ok(service.scheduleHearing(req));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLERK','JUDGE','COMPLIANCE','AUDITOR')")
    public ResponseEntity<List<HearingResponse>> getAll() { return ResponseEntity.ok(service.getAllHearings()); }

    @GetMapping("/{id}")
    public ResponseEntity<HearingResponse> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getHearingById(id)); }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<HearingResponse>> getByCase(@PathVariable Long caseId) { return ResponseEntity.ok(service.getHearingsByCase(caseId)); }

    @GetMapping("/judge/{judgeId}")
    public ResponseEntity<List<HearingResponse>> getByJudge(@PathVariable Long judgeId) { return ResponseEntity.ok(service.getHearingsByJudge(judgeId)); }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','JUDGE')")
    public ResponseEntity<HearingResponse> updateStatus(@PathVariable Long id, @RequestParam Hearing.HearingStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PostMapping("/{id}/proceedings")
    @PreAuthorize("hasAnyRole('ADMIN','JUDGE')")
    public ResponseEntity<ProceedingResponse> addProceeding(@PathVariable Long id, @RequestBody ProceedingRequest req) {
        req.setHearingId(id);
        return ResponseEntity.ok(service.addProceeding(req));
    }

    @GetMapping("/{id}/proceedings")
    public ResponseEntity<List<ProceedingResponse>> getProceedings(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProceedingsByHearing(id));
    }
}
