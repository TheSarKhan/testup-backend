package com.exam.examapp.security.service.interfaces;

import com.exam.examapp.model.enums.Role;
import com.exam.examapp.security.dto.request.LoginRequest;
import com.exam.examapp.security.dto.request.RegisterRequest;
import com.exam.examapp.security.dto.request.ResetPasswordRequest;
import com.exam.examapp.security.dto.response.TokenResponse;

public interface AuthService {
    String register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    String logout(String username);

    TokenResponse refresh(String refreshToken);

    String forgetPassword(String email);

    String verifyEmailCode(String email, String  code);

    String resetPassword(ResetPasswordRequest request);

    TokenResponse resetCurrentUserPassword(String oldPassword, String newPassword);

    TokenResponse finishRegister(Role role, boolean isAcceptedTerms);
}