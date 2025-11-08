package com.exam.examapp.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StudentExamSaveRequest(@NotNull UUID studentExamId,
                                     @NotNull UUID questionId,
                                     String answer) {
}
