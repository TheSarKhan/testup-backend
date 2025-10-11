package com.exam.examapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MultiNotificationRequest(
        @NotBlank
        @Schema(defaultValue = "Notification Title")
        String title,
        @NotBlank
        @Schema(defaultValue = "Notification Message")
        String message,
        List<String> emails
) {
}
