package com.exam.examapp.dto.response;

import com.exam.examapp.model.enums.ExamStatus;

import java.util.UUID;

public record StudentExamResponse(
        UUID id,
        ExamStatus status,
        ExamBlockResponse examResponse
) {}
