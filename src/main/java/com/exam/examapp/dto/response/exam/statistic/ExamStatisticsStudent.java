package com.exam.examapp.dto.response.exam.statistic;

import java.util.UUID;

public record ExamStatisticsStudent(
        UUID id,
        UUID studentExamId,
        String name,
        long durationInSeconds,
        double score,
        int totalQuestionCount,
        int correctQuestionCount,
        boolean hasUncheckedQuestion
) {
}
