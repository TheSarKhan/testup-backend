package com.exam.examapp.dto.request.payment;

import java.time.Instant;
import java.util.Map;

public record PaymentCallbackRequest(
        String orderId,
        Double amount,
        String currency,
        String status,
        String transactionId,
        String description,
        Map<String, String> extraData,
        Instant date) {
}