package com.exam.examapp.dto.request;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record QuestionUpdateRequest(
        @NotNull
        UUID id,
        String title,
        String titleDescription,
        boolean isTitlePicture,
        boolean isTitleContainMath,
        @NotNull QuestionType type,
        Difficulty difficulty,
        UUID topicId,
        int questionCount,
        List<QuestionUpdateRequest> questions,
        String soundUrl,
        @NotNull QuestionDetails questionDetails
) {
}
