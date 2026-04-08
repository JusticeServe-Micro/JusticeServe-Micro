package com.justiceserve.identityservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required") private String name;
    @NotBlank @Email(message = "Valid email required") private String email;
    @NotBlank(message = "Password is required") private String password;
    private String phone;
}
