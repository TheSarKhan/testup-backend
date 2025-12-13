package com.exam.examapp.dto.response.subject;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.subject.Topic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuestionResponse(
        UUID id,
        String title,
        String titleDescription,
        boolean isTitlePicture,
        boolean isTitleContainMath,
        QuestionType type,
        Difficulty difficulty,
        Topic topic,
        String soundUrl,
        int questionCount,
        List<QuestionResponse> questions,
        QuestionDetails questionDetails,
        Instant createdAt,
        Instant updatedAt
) {
}
