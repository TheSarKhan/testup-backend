package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.payment.PaymentRequest;

public interface PaymentService {
    String initPayment(PaymentRequest request);

    void updateResults(String uuid);
}