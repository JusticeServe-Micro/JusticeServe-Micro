package com.justiceserve.reportservice.controller;

import com.justiceserve.reportservice.entity.Report;
import com.justiceserve.reportservice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ReportController — Member M6
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportRepository repo;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE','AUDITOR')")
    public ResponseEntity<Report> generate(@RequestBody Map<String, Object> req) {
        Report r = Report.builder()
                .scope(Report.ReportScope.valueOf(req.get("scope").toString()))
                .metrics(req.getOrDefault("metrics", "{}").toString())
                .generatedBy(req.get("generatedBy") != null ? Long.valueOf(req.get("generatedBy").toString()) : null)
                .build();
        return ResponseEntity.ok(repo.save(r));
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<Report>> getByScope(@PathVariable Report.ReportScope scope) {
        return ResponseEntity.ok(repo.findByScope(scope));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getById(@PathVariable Long id) {
        return ResponseEntity.ok(repo.findById(id).orElseThrow());
    }
}