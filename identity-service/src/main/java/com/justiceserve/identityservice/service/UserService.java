package com.justiceserve.identityservice.service;

import com.justiceserve.identityservice.dto.UserResponse;
import com.justiceserve.identityservice.entity.User;

import java.util.List;
public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    List<UserResponse> getUsersByRole(User.Role role);
    UserResponse updateUserStatus(Long id, User.Status status);
    UserResponse updateUserRole(Long id, User.Role role);
    void deleteUser(Long id);
}
