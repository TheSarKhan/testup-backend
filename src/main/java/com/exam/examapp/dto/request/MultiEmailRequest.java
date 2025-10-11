package com.exam.examapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MultiEmailRequest(
        @NotBlank
        @Schema(defaultValue = "Email Title")
        String title,
        @NotBlank
        @Schema(defaultValue = "Email Message")
        String message,
        List<String> emails
) {
}
