package com.justiceserve.identityservice.service.impl;

import com.justiceserve.identityservice.dto.UserResponse;
import com.justiceserve.identityservice.entity.User;
import com.justiceserve.identityservice.exception.ResourceNotFoundException;
import com.justiceserve.identityservice.feign.AuditFeignClient;
import com.justiceserve.identityservice.repository.UserRepository;
import com.justiceserve.identityservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker(name = "auditServiceBreaker", fallbackMethod = "updateUserStatusFallback")
    public UserResponse updateUserStatus(Long id, User.Status status) {
        var u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        User.Status old = u.getStatus();
        u.setStatus(status);

        UserResponse r = UserResponse.from(repo.save(u));

        auditClient.log(id, "USER_STATUS_CHANGED", "User:" + id + " [" + old + " -> " + status + "]");

        return r;
    }

    // Fallback for updateUserStatus
    public UserResponse updateUserStatusFallback(Long id, User.Status status, Throwable t) {
        log.warn("Audit service unavailable during status update for User #{}. Fallback triggered. Error: {}", id, t.getMessage());
        // Because repo.save() succeeded before the audit call failed, the DB is already updated.
        // We simply retrieve the updated record and return it to the client so they don't see an error.
        return repo.findById(id).map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found during fallback: " + id));
    }

    @Override
    @CircuitBreaker(name = "auditServiceBreaker", fallbackMethod = "updateUserRoleFallback")
    public UserResponse updateUserRole(Long id, User.Role role) {
        var u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        User.Role old = u.getRole();
        u.setRole(role);

        UserResponse r = UserResponse.from(repo.save(u));
        log.info("Role changed #{}: {} -> {}", id, old, role);

        auditClient.log(id, "USER_ROLE_CHANGED", "User:" + id + " [" + old + " -> " + role + "]");

        return r;
    }

    // Fallback for updateUserRole
    public UserResponse updateUserRoleFallback(Long id, User.Role role, Throwable t) {
        log.warn("Audit service unavailable during role update for User #{}. Fallback triggered. Error: {}", id, t.getMessage());
        return repo.findById(id).map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found during fallback: " + id));
    }

    @Override
    @CircuitBreaker(name = "auditServiceBreaker", fallbackMethod = "deleteUserFallback")
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("User not found: " + id);

        // Let the potential failure bubble up
        auditClient.log(id, "USER_DELETED", "User:" + id + " deleted");

        repo.deleteById(id);
    }

    // Fallback for deleteUser
    public void deleteUserFallback(Long id, Throwable t) {
        log.warn("Audit service unavailable during deletion of User #{}. Fallback triggered. Error: {}", id, t.getMessage());
        // Note: Because the audit call happened BEFORE repo.deleteById in the main method,
        // a failure in the audit call prevents the deletion.
        // We execute the deletion here in the fallback to ensure the primary business logic succeeds!
        repo.deleteById(id);
    }
}