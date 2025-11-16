package com.exam.examapp.dto;

import java.time.Instant;
import java.util.UUID;

public record CurrentExam(
        Instant startTime,
        long durationInSeconds,
        UUID studentExamId,
        UUID startId,
        UUID examId
) {
}
