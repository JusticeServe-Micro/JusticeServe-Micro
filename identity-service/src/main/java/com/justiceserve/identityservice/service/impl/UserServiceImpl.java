package com.justiceserve.identityservice.service.impl;

import com.justiceserve.identityservice.dto.UserResponse;
import com.justiceserve.identityservice.entity.User;
import com.justiceserve.identityservice.exception.ResourceNotFoundException;
import com.justiceserve.identityservice.feign.AuditFeignClient;
import com.justiceserve.identityservice.repository.UserRepository;
import com.justiceserve.identityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final AuditFeignClient auditClient;

    @Override
    public List<UserResponse> getAllUsers() {
        return repo.findAll().stream().map(UserResponse::from).toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return UserResponse.from(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }

    @Override
    public List<UserResponse> getUsersByRole(User.Role role) {
        return repo.findByRole(role).stream().map(UserResponse::from).toList();
    }

    @Override
    public UserResponse updateUserStatus(Long id, User.Status status) {
        var u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        User.Status old = u.getStatus();
        u.setStatus(status);
        UserResponse r = UserResponse.from(repo.save(u));
        try {
            auditClient.log(id, "USER_STATUS_CHANGED", "User:" + id + " [" + old + " -> " + status + "]");
        } catch (Exception e) {
            log.warn("Audit: {}", e.getMessage());
        }
        return r;
    }

    @Override
    public UserResponse updateUserRole(Long id, User.Role role) {
        var u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        User.Role old = u.getRole();
        u.setRole(role);
        UserResponse r = UserResponse.from(repo.save(u));
        log.info("Role changed #{}: {} -> {}", id, old, role);
        try {
            auditClient.log(id, "USER_ROLE_CHANGED", "User:" + id + " [" + old + " -> " + role + "]");
        } catch (Exception e) {
            log.warn("Audit: {}", e.getMessage());
        }
        return r;
    }

    @Override
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("User not found: " + id);
        try {
            auditClient.log(id, "USER_DELETED", "User:" + id + " deleted");
        } catch (Exception e) {
            log.warn("Audit: {}", e.getMessage());
        }
        repo.deleteById(id);
    }
}
