package com.exam.examapp.dto.request.information;

import com.exam.examapp.model.enums.PageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MediaContentUpdateRequest(
        @NotNull
        UUID id,
        @NotBlank
        @Schema(defaultValue = "Media Content Text")
        String text,
        @NotBlank
        @Schema(defaultValue = "Author")
        String author,
        @NotNull
        @Schema(defaultValue = "rgba(0,0,0,1)")
        String backgroundColor,
        @NotNull
        @Schema(defaultValue = "rgba(255,255,255,1)")
        String textColor,
        @NotNull
        @Schema(defaultValue = "HOME_BANNER")
        PageType pageType
) {
}
