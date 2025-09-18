package com.exam.examapp.dto.response;

public record PaymentInvoiceResponse(
        String code,
        String message,
        String route,
        String internalMessage,
        String responseId,
        Payload payload
) {

    public record Payload(
            Long id,
            String merchantId,
            Double amount,
            Double payriffAmount,
            Double payriffFixedFeeAmount,
            Double payriffFee,
            Double totalAmount,
            String fullName,
            String email,
            String phoneNumber,
            String personalCode,
            String expireDate,
            String currencyType,
            String languageType,
            String paymentType,
            String subscriptionState,
            String paymentDay,
            String expireDay,
            Boolean active,
            String description,
            String approveURL,
            String cancelURL,
            String declineURL,
            String uuid,
            String invoiceUuid,
            String baseUrl,
            Long invoiceCode,
            String invoiceStatus,
            String installmentProductType,
            Integer installmentPeriod,
            String source,
            Boolean directPay,
            String createdDate
    ) {
    }
}
