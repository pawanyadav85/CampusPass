package com.campuspass.service;

import com.campuspass.dto.request.ChangePasswordRequest;
import com.campuspass.dto.request.LoginRequest;
import com.campuspass.dto.request.StudentRegisterRequest;
import com.campuspass.dto.response.AuthResponse;
import com.campuspass.security.UserPrincipal;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse registerStudent(StudentRegisterRequest request);
    AuthResponse getCurrentUser(UserPrincipal principal);
    void changePassword(Long userId, ChangePasswordRequest request);
}
