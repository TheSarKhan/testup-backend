package com.exam.examapp.dto.request;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubjectStructureQuestionsRequest(
        @NotNull
        SubjectStructureRequest subjectStructureRequest,
        @NotNull
        List<QuestionRequest> questionRequests
) {}
