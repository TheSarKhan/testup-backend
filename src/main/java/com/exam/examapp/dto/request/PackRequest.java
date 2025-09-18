package com.exam.examapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PackRequest(
        @NotBlank
        @Schema(defaultValue = "Pack Header")
        String header,
        @NotBlank
        @Schema(defaultValue = "Pack Name")
        String packName,
        @NotNull
        @Schema(defaultValue = "10")
        BigDecimal packPrice,
        @Schema(defaultValue = "10")
        int monthlyExamCount,
        @Schema(defaultValue = "10")
        int questionCountPerExam,
        @Schema(defaultValue = "10")
        int totalExamCount,
        @Schema(defaultValue = "10")
        int studentPerExam,
        boolean canAnalysisStudentResults,
        boolean canEditExam,
        boolean canAddPicture,
        boolean canAddPdfSound,
        boolean canShareViaCode,
        boolean canDownloadExamAsPdf,
        boolean canAddMultipleSubjectInOneExam,
        boolean canUseExamTemplate,
        boolean canAddManualCheckAutoQuestion,
        boolean canSelectExamDuration,
        boolean canUseQuestionDb,
        boolean canPrepareQuestionsDb
) {
}
