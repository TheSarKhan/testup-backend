package com.exam.examapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationUpdateRequest(
        @NotNull
        UUID id,
        @NotBlank
        @Schema(defaultValue = "Notification Title")
        String title,
        @NotBlank
        @Schema(defaultValue = "Notification Message")
        String message,
        @Email
        @Schema(defaultValue = "admin1234@gmail.com")
        String email
) {
}
