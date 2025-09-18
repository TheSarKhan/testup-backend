package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.PaymentResultResponse;
import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import com.exam.examapp.repository.PaymentResultRepository;
import com.exam.examapp.service.interfaces.PaymentResultService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentResultServiceImpl implements PaymentResultService {
    private final PaymentResultRepository paymentResultRepository;

    private final PaymentServiceImpl paymentService;

    private final UserService userService;

    @Override
    public List<PaymentResultResponse> getAllPaymentResults() {
        List<PaymentResult> paymentResults = paymentResultRepository.findAll();
        for (PaymentResult paymentResult : paymentResults)
            paymentService.updateResults(paymentResult.getUuid());

        return paymentResults.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResultResponse> getMyPaymentResults() {
        User user = userService.getCurrentUser();
        List<PaymentResult> paymentResults = paymentResultRepository.getByUser(user);
        for (PaymentResult paymentResult : paymentResults)
            paymentService.updateResults(paymentResult.getUuid());

        return paymentResults.stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResultResponse toResponse(PaymentResult paymentResult) {
        return new PaymentResultResponse(
                paymentResult.getStatus(),
                paymentResult.getAmount(),
                paymentResult.getCurrency(),
                paymentResult.getDescription(),
                paymentResult.getPaymentDay());
    }
}
