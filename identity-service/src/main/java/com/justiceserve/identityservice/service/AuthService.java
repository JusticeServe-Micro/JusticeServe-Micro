package com.justiceserve.identityservice.service;

import com.justiceserve.identityservice.dto.AuthResponse;
import com.justiceserve.identityservice.dto.LoginRequest;
import com.justiceserve.identityservice.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
}
