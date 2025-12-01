package com.exam.examapp.dto.response.subject;

import com.exam.examapp.dto.response.QuestionResponseWithoutAnswer;
import com.exam.examapp.model.subject.SubjectStructure;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubjectStructureQuestionResponseWithoutAnswer(
        UUID id,
        SubjectStructure subjectStructure,
        List<QuestionResponseWithoutAnswer> question,
        Instant createdAt,
        Instant updatedAt
) {
}
