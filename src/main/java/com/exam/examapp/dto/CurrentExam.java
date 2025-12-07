package com.exam.examapp.dto;

import java.time.Instant;
import java.util.UUID;

public record CurrentExam(
        Instant startTime,
        Integer durationInSeconds,
        UUID studentExamId,
        UUID startId,
        UUID examId
) {
}
