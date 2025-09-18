package com.exam.examapp.dto.request.information;

import com.exam.examapp.model.enums.SuperiorityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SuperiorityRequest(
    @NotBlank
    @Schema(defaultValue = "Superiority Text")
    String text,
    @NotNull
    @Schema(defaultValue = "ADVANTAGES_FOR_STUDENT")
    SuperiorityType type
) {
}
