package com.exam.examapp.dto.request.exam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ExamTemplateUpdateRequest(
    @NotNull UUID id,
    @NotNull UUID subjectStructureId,
    @Valid ExamTemplateRequest examTemplateRequest) {}
