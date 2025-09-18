package com.exam.examapp.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ExamUpdateRequest(@NotNull UUID id, @NotNull ExamRequest request
    //        @NotNull List<SubjectStructureQuestionsUpdateRequest> subjectStructures,
    //        @NotBlank @Schema(defaultValue = "Custom Ielts") String examTitle,
    //        @Schema(defaultValue = "Custom Ielts description") String examDescription,
    //        UUID headerTagId,
    //        List<UUID> otherTagIds,
    //        @Min(30) @Schema(defaultValue = "300") Integer durationInSeconds,
    //        @Schema(description = "Cost for only admin if other teacher create please give null.")
    //        BigDecimal cost,
    //        boolean isHidden,
    //        boolean hasSound,
    //        boolean hasPicture,
    //        boolean hasPdfPicture
    ) {}
