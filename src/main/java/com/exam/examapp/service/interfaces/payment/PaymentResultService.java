package com.exam.examapp.service.interfaces.payment;

import com.exam.examapp.dto.response.PaymentResultResponse;

import java.util.List;

public interface PaymentResultService {
    List<PaymentResultResponse> getAllPaymentResults();

    List<PaymentResultResponse> getMyPaymentResults();
}
