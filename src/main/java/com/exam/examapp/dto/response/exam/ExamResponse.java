package com.exam.examapp.dto.response.exam;

import com.exam.examapp.dto.response.UserResponseForExam;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExamResponse(
    UUID id,
    String examTitle,
    Tag headerTag,
    List<Tag> otherTags,
    int durationInSeconds,
    BigDecimal cost,
    double rating,
    UserResponseForExam teacher,
    List<SubjectStructureQuestion> subjectStructureQuestion,
    String examDescription,
    Boolean hasUncheckedAnswer,
    String explanationVideoUrl,
    int totalQuestionCount,
    boolean isHidden,
    boolean isActive,
    Instant createAt,
    Instant updatedAt) {}
