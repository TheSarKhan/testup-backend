package com.exam.examapp.dto.request.information;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AdvertisementUpdateRequest(
        @NotNull
        UUID id,
        @NotBlank
        @Schema(defaultValue = "Advertisement Title")
        String title,
        @Schema(defaultValue = "Advertisement description")
        String description,
        @NotBlank
        String backgroundColor,
        @NotBlank
        String buttonColor,
        @NotBlank
        String textColor,
        @NotBlank
        @Schema(defaultValue = "www.youtube.com")
        String redirectUrl
) {
}
