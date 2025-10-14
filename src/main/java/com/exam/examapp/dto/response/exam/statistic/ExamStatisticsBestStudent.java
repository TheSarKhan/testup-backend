package com.exam.examapp.dto.response.exam.statistic;

import java.util.UUID;

public record ExamStatisticsBestStudent(
        UUID id,
        UUID studentExamId,
        String name,
        Long durationInSeconds,
        double score
) {
}
