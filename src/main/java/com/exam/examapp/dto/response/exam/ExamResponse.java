package com.exam.examapp.dto.response.exam;

import com.exam.examapp.dto.response.SubjectStructureQuestionResponse;
import com.exam.examapp.dto.response.UserResponseForExam;
import com.exam.examapp.model.Tag;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExamResponse(
    UUID id,
    String examTitle,
    Tag headerTag,
    List<Tag> otherTags,
    Integer durationInSeconds,
    BigDecimal cost,
    Double rating,
    UserResponseForExam teacher,
    List<SubjectStructureQuestionResponse> subjectStructureQuestion,
    String examDescription,
    Boolean hasUncheckedAnswer,
    String explanationVideoUrl,
    Integer totalQuestionCount,
    Boolean isReadyForSale,
    Boolean isHidden,
    Boolean isActive,
    Instant createAt,
    Instant updatedAt) {}
