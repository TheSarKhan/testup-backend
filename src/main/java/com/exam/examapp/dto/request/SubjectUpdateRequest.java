package com.exam.examapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubjectUpdateRequest(@NotNull
                                   UUID id,
                                   @NotBlank
                                   @Schema(defaultValue = "Subject name")
                                   String name,
                                   @Schema(defaultValue = "false")
                                   boolean isSupportMath) {
}
