package com.exam.examapp.dto.response;

import com.exam.examapp.model.Tag;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExamBlockResponse(
        UUID id,
        String examTitle,
        Tag headerTag,
        List<Tag> otherTags,
        int durationInSeconds,
        BigDecimal cost,
        double rating,
        boolean isHidden,
        int totalQuestionCount,
        Boolean hasUncheckedAnswer,
        Instant createAt,
        Instant updatedAt
) {}
