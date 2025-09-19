package com.exam.examapp.dto.response;

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
