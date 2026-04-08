package com.justiceserve.identityservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private String role;
    public AuthResponse(String token, Long userId, String name, String email, String role) {
        this.token=token; this.tokenType="Bearer"; this.userId=userId; this.name=name; this.email=email; this.role=role;
    }
}
