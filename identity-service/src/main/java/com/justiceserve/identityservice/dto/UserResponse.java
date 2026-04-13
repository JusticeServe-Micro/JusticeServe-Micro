package com.justiceserve.identityservice.dto;

import com.justiceserve.identityservice.entity.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long userId;
    private String name;
    private User.Role role;
    private String email;
    private String phone;
    private User.Status status;

    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.userId = u.getUserId();
        r.name = u.getName();
        r.role = u.getRole();
        r.email = u.getEmail();
        r.phone = u.getPhone();
        r.status = u.getStatus();
        return r;
    }
}
