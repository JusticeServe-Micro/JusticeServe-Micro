package com.justiceserve.caseservice.controller;

import com.justiceserve.caseservice.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    public ResponseEntity<String> fileCase(@RequestBody String caseDetails) {
        return new ResponseEntity<>("Case filed successfully", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<String> getAll() {
        return ResponseEntity.ok("List of cases");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable Long id) {
        return ResponseEntity.ok("Case details");
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<String> getByCitizenId(@PathVariable Long citizenId) {
        return ResponseEntity.ok("Cases for citizen");
    }



}
