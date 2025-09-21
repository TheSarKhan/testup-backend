package com.exam.examapp.dto.response.exam;

import com.exam.examapp.model.enums.ExamStatus;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record StartExamResponse(
    UUID studentExamId,
    ExamStatus status,
    Map<UUID, String> questionIdToAnswerMap,
    Map<UUID, Integer> listeningIdToPlayTimeMap,
    Instant startTime,
    ExamResponse exam) {}
