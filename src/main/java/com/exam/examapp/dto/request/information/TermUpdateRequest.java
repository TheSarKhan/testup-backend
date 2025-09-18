package com.exam.examapp.dto.request.information;

import com.exam.examapp.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TermUpdateRequest(
        @NotNull
        UUID id,
        @NotBlank
        @Schema(defaultValue = "Term Name")
        String termName,
        @NotBlank
        @Schema(defaultValue = "Term Description")
        String description,
        @NotNull
        @Schema(defaultValue = "STUDENT")
        Role role) {
}
