package com.exam.examapp.dto.request.subject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TopicUpdateRequest(
        @NotNull
        UUID id,
        @NotNull
        UUID subjectId,
        @NotBlank
        @Schema(example = "Advanced Algebra")
        String name
) {

}
