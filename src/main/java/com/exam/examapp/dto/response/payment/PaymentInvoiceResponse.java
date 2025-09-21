package com.exam.examapp.dto.response.payment;

public record PaymentInvoiceResponse(
        String message,
        Payload payload
) {
    public record Payload(
            String invoiceStatus,
            String createdDate
    ) {
    }
}
