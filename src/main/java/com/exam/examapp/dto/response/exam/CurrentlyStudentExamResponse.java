package com.exam.examapp.dto.response.exam;

import java.time.Instant;
import java.util.UUID;

public record CurrentlyStudentExamResponse(
        Instant startTime,
        long durationInSeconds,
        UUID studentExamId,
        UUID examId
) {
}
