package com.exam.examapp.dto.response;

public record PaymentInitResponse(String code,
                                  String message,
                                  Payload payload) {

    public record Payload(String invoiceUuid, String uuid, String paymentUrl) {}
}