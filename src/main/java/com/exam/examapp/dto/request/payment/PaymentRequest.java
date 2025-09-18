package com.exam.examapp.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentRequest(@NotNull @Schema(defaultValue = "10") double amount,
                             @NotNull @Schema(defaultValue = "AZN") String currency,
                             @NotNull UUID productId) {
}
