package com.exam.examapp.security.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.security.dto.request.LoginRequest;
import com.exam.examapp.security.dto.request.RegisterRequest;
import com.exam.examapp.security.dto.request.ResetPasswordRequest;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.security.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication operations")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register",
            description = "Creates new account and returns a success message")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody
                                                        @Valid
                                                        RegisterRequest request) {
        String register = authService.register(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        register,
                        null));
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user",
            description = "Authenticates the user and returns an access and refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody
                                                            @Valid
                                                            LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Login successful",
                        tokenResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout a user",
            description = "Logs out the user and invalidates the session")
    public ResponseEntity<ApiResponse<String>> logout(@RequestParam
                                                      @Schema(defaultValue = "example@gmail.com")
                                                      String email) {
        String logoutMessage = authService.logout(email);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        logoutMessage,
                        null));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
            description = "Generates a new access token using the refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestParam
                                                              String refreshToken) {
        TokenResponse refresh = authService.refresh(refreshToken);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Refresh successful",
                        refresh));
    }

    @PostMapping("/forget-password")
    @Operation(summary = "Forget password",
            description = "Sends a password reset code to the provided email address")
    public ResponseEntity<ApiResponse<String>> forgetPassword(@RequestParam
                                                              @Email
                                                              @Schema(defaultValue = "example@gmail.com")
                                                              String email) {
        String message = authService.forgetPassword(email);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        message,
                        null));
    }

    @PostMapping("/verify-email-code")
    @Operation(summary = "Verify email code",
            description = "Verifies the code sent to the user's email for password reset or account verification")
    public ResponseEntity<ApiResponse<String>> verifyEmailCode(@RequestParam
                                                               @Email
                                                               @Schema(defaultValue = "example@gmail.com")
                                                               String email,
                                                               @RequestParam
                                                               @Schema(defaultValue = "1234")
                                                               String code) {
        String message = authService.verifyEmailCode(email, code);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Verification code verified successfully",
                        message));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password",
            description = "Resets the user's password using email, new password, and a valid reset identifier (UUID)")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        String message = authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        message,
                        null));
    }

    @PostMapping("/reset-password-current")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reset password",
            description = "Resets the current user's password using email, new password.")
    public ResponseEntity<ApiResponse<TokenResponse>> resetPassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        TokenResponse tokenResponse = authService.resetCurrentUserPassword(oldPassword, newPassword);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Current password reset successful",
                        tokenResponse));
    }

    @PatchMapping("/finish-register")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<TokenResponse>> finishRegister(@RequestParam
                                                                     @NotNull
                                                                     @Schema(defaultValue = "STUDENT")
                                                                     Role role,
                                                                     @RequestParam
                                                                     @Schema(defaultValue = "true")
                                                                     boolean isAcceptedTerms) {
        TokenResponse tokenResponse = authService.finishRegister(role, isAcceptedTerms);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Registration successful",
                        tokenResponse));
    }
}
