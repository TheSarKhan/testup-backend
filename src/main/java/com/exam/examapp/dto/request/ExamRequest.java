package com.exam.examapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExamRequest(
    @NotNull List<SubjectStructureQuestionsRequest> subjectStructures,
    @NotBlank @Schema(defaultValue = "Custom Ielts") String examTitle,
    @Schema(defaultValue = "Custom Ielts description") String examDescription,
    UUID headerTagId,
    List<UUID> otherTagIds,
    @Min(30) @Schema(defaultValue = "300") Integer durationInSeconds,
    @Schema(description = "Cost for only admin if other teacher create please give null.")
        BigDecimal cost,
    boolean isHidden,
    boolean hasSound,
    boolean hasPicture,
    boolean hasPdfPicture,
    String explanationVideoUrl) {}
