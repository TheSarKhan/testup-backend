package com.exam.examapp.dto.request;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ExamTemplateRequest(
    @NotNull UUID submoduleId, @Valid SubjectStructureRequest subjectStructureRequest) {}
