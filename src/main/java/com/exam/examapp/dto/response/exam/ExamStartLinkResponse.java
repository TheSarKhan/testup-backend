package com.exam.examapp.dto.response.exam;

import java.util.List;
import java.util.UUID;

public record ExamStartLinkResponse(
        UUID id,
        UUID startId,
        String examTitle,
        Integer durationInSeconds,
        Integer totalQuestionCount,
        List<String> subjectNames,
        boolean isHidden
) {
}
