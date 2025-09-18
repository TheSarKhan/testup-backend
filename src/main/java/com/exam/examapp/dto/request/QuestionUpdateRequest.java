package com.exam.examapp.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record QuestionUpdateRequest(
        @NotNull
        UUID id,
        @NotNull
        QuestionRequest question
) {
}
