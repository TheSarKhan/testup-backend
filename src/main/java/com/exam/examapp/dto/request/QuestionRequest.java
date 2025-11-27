package com.exam.examapp.dto.request;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record QuestionRequest(
        String title,
        String titleDescription,
        boolean isTitlePicture,
        String mathTitle,
        @NotNull QuestionType type,
        Difficulty difficulty,
        UUID topicId,
        int questionCount,
        List<QuestionRequest> questions,
        @NotNull QuestionDetails questionDetails) {
}
