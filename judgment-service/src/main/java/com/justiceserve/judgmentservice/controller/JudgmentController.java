package com.justiceserve.judgmentservice.controller;

import com.justiceserve.judgmentservice.dto.*;
import com.justiceserve.judgmentservice.service.impl.JudgmentServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/judgments")
@RequiredArgsConstructor
@Tag(name = "Judgment Management")
public class JudgmentController {
    private final JudgmentServiceImpl service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','JUDGE')")
    public ResponseEntity<JudgmentResponse> record(@Valid @RequestBody JudgmentRequest req) {
        return ResponseEntity.ok(service.recordJudgment(req));
    }

    @GetMapping
    public ResponseEntity<List<JudgmentResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JudgmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<JudgmentResponse>> getByCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(service.getByCase(caseId));
    }

    @PatchMapping("/{id}/finalize")
    @PreAuthorize("hasAnyRole('ADMIN','JUDGE')")
    public ResponseEntity<JudgmentResponse> finalize(@PathVariable Long id) {
        return ResponseEntity.ok(service.finalizeJudgment(id));
    }
}