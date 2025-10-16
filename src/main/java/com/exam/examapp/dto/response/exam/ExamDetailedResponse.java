package com.exam.examapp.dto.response.exam;

import com.exam.examapp.model.Tag;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.subject.Subject;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExamDetailedResponse(
        UUID id,
        String examTitle,
        Tag headerTag,
        List<Tag> otherTags,
        List<Subject> subjects,
        Integer durationInSeconds,
        BigDecimal cost,
        Double rating,
        Boolean isHidden,
        Integer totalQuestionCount,
        Boolean hasUncheckedAnswer,
        Boolean isReadyForSale,
        ExamStatus examStatus,
        Instant createAt,
        Instant updatedAt
) {}