package com.exam.examapp.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(
        @Email
        @Schema(defaultValue = "example@gmail.com")
        String email,
        @NotBlank
        @Schema(defaultValue = "Example123")
        String password,
        @NotNull
        String uuid
) {
}
