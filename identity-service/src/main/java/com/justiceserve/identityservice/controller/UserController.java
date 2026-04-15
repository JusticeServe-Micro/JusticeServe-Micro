package com.justiceserve.identityservice.controller;

import com.justiceserve.identityservice.dto.UserResponse;
import com.justiceserve.identityservice.entity.User;
import com.justiceserve.identityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CITIZEN','JUDGE','CLERK','LAWYER','AUDITOR','COMPLIANCE')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK','JUDGE','CITIZEN')")
    public ResponseEntity<List<UserResponse>> getByRole(@PathVariable User.Role role) {
        log.error("I am reached Fetching users with role: {}", role);
        return ResponseEntity.ok(service.getUsersByRole(role));
    }

    @PatchMapping("/{id}/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.updateUserRole(id, User.Role.valueOf(body.get("role").toUpperCase())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id, @RequestParam User.Status status) {
        return ResponseEntity.ok(service.updateUserStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
