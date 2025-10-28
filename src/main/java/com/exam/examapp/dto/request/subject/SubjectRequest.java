package com.exam.examapp.dto.request.subject;

import jakarta.validation.constraints.NotBlank;

public record SubjectRequest(
        @NotBlank String name,
        boolean isSupportMath) {
}
