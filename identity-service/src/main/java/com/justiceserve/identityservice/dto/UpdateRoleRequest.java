package com.justiceserve.identityservice.dto;
import com.justiceserve.identityservice.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull private User.Role role;
}
