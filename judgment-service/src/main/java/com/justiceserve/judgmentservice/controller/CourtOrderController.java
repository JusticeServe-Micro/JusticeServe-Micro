package com.justiceserve.judgmentservice.controller;

import com.justiceserve.judgmentservice.dto.*;
import com.justiceserve.judgmentservice.entity.CourtOrder;
import com.justiceserve.judgmentservice.service.impl.JudgmentServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/court-orders")
@RequiredArgsConstructor
@Tag(name = "Court Orders")
public class CourtOrderController {
    private final JudgmentServiceImpl service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','JUDGE')")
    public ResponseEntity<CourtOrderResponse> issue(@RequestBody CourtOrderRequest req) {
        return ResponseEntity.ok(service.issueOrder(req));
    }

    @GetMapping
    public ResponseEntity<List<CourtOrderResponse>> getAll() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtOrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrderById(id));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<CourtOrderResponse>> getByCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(service.getOrdersByCase(caseId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','JUDGE')")
    public ResponseEntity<CourtOrderResponse> updateStatus(@PathVariable Long id, @RequestParam CourtOrder.OrderStatus status) {
        return ResponseEntity.ok(service.updateOrderStatus(id, status));
    }
}