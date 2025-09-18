package com.exam.examapp.dto.request.information;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ContactUpdateRequest(
        @NotBlank
        @Schema(defaultValue = "Contact Title")
        String title,
        @NotBlank
        @Schema(defaultValue = "Contact Description")
        String description,
        @NotBlank
        String phone,
        @NotBlank
        String email,
        @NotNull
        List<String> contactNames,
        @NotNull
        List<String> contactRedirectUrls
) {
}
