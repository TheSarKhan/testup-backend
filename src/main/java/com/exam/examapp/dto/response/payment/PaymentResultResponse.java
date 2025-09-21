package com.exam.examapp.dto.response.payment;

public record PaymentResultResponse(
        String status,
        double amount,
        String currency,
        String description,
        String paymentCreateDate
) {
}