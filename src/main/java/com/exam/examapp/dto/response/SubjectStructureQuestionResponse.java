package com.exam.examapp.dto.response;

import com.exam.examapp.dto.response.subject.QuestionResponse;
import com.exam.examapp.model.subject.SubjectStructure;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubjectStructureQuestionResponse(
        UUID id,

        SubjectStructure subjectStructure,

        List<QuestionResponse> questionResponse,

        Instant createdAt,

        Instant updatedAt
) {
}
