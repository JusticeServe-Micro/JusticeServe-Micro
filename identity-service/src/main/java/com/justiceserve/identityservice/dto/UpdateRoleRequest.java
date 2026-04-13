package com.justiceserve.identityservice.dto;

import com.justiceserve.identityservice.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "Role must not be null")
    private User.Role role;
}
