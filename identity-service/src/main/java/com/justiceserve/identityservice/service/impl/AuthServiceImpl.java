package com.justiceserve.identityservice.service.impl;

import com.justiceserve.identityservice.dto.AuthResponse;
import com.justiceserve.identityservice.dto.LoginRequest;
import com.justiceserve.identityservice.dto.RegisterRequest;
import com.justiceserve.identityservice.entity.User;
import com.justiceserve.identityservice.exception.BadRequestException;
import com.justiceserve.identityservice.exception.ResourceNotFoundException;
import com.justiceserve.identityservice.feign.AuditFeignClient;
import com.justiceserve.identityservice.repository.UserRepository;
import com.justiceserve.identityservice.security.JwtUtils;
import com.justiceserve.identityservice.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authManager;
    private final AuditFeignClient auditClient;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        log.info("Registering user: {}", req.getEmail());

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered: " + req.getEmail());
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(User.Role.CITIZEN)
                .phone(req.getPhone())
                .status(User.Status.ACTIVE)
                .build();

        user = userRepo.save(user);
        log.info("User saved with ID: {}", user.getUserId());

        handleAudit(user.getUserId(), "USER_REGISTERED", "Email: " + user.getEmail());

        return mapToAuthResponse(user, jwtUtils.generateToken(user));
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        log.info("Login attempt: {}", req.getEmail());

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + req.getEmail()));

        handleAudit(user.getUserId(), "USER_LOGIN", "Login successful");

        return mapToAuthResponse(user, jwtUtils.generateToken(user));
    }

    private AuthResponse mapToAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private void handleAudit(Long userId, String action, String detail) {
        try {
            auditClient.log(userId, action, detail);
        } catch (Exception e) {
            log.warn("Non-blocking Audit failure: {}", e.getMessage());
        }
    }
}