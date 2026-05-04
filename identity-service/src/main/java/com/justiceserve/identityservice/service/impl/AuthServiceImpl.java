package com.justiceserve.identityservice.service.impl;

import com.justiceserve.identityservice.dto.*;
import com.justiceserve.identityservice.entity.User;
import com.justiceserve.identityservice.exception.BadRequestException;
import com.justiceserve.identityservice.feign.AuditFeignClient;
import com.justiceserve.identityservice.repository.UserRepository;
import com.justiceserve.identityservice.security.JwtUtils;
import com.justiceserve.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public AuthResponse register(RegisterRequest req) {
        log.info("Register: {}", req.getEmail());
        if (userRepo.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already registered: " + req.getEmail());
        User user = User.builder()
                .name(req.getName()).email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(User.Role.CITIZEN).phone(req.getPhone())
                .status(User.Status.ACTIVE).build();
        user = userRepo.save(user);
        log.info("Registered userId={}", user.getUserId());
        try {
            auditClient.log(user.getUserId(), "USER_REGISTERED", "User:" + user.getUserId() + " email=" + user.getEmail());
        } catch (Exception e) {
            log.warn("Audit failed: {}", e.getMessage());
        }
        return new AuthResponse(jwtUtils.generateToken(user), user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        log.info("Login: {}", req.getEmail());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepo.findByEmail(req.getEmail()).orElseThrow(() -> new BadRequestException("User not found"));
        log.info("Login OK userId={}, role={}", user.getUserId(), user.getRole());
        try {
            auditClient.log(user.getUserId(), "USER_LOGIN", "User:" + user.getUserId() + " role=" + user.getRole());
        } catch (Exception e) {
            log.warn("Audit failed: {}", e.getMessage());
        }
        return new AuthResponse(jwtUtils.generateToken(user), user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
