package com.exam.examapp.dto.request.subject;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SubjectStructureUpdateRequest(
    @NotNull UUID id, @Valid SubjectStructureRequest request) {}
