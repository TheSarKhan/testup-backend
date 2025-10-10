package com.exam.examapp.service.impl.payment;

import com.exam.examapp.dto.response.payment.PaymentResultResponse;
import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import com.exam.examapp.repository.PaymentResultRepository;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.payment.PaymentResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentResultServiceImpl implements PaymentResultService {
    private final PaymentResultRepository paymentResultRepository;

    private final PaymentServiceImpl paymentService;

    private final UserService userService;

    @Override
    public List<PaymentResultResponse> getAllPaymentResults() {
        return paymentResultRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResultResponse> getMyPaymentResults() {
        User user = userService.getCurrentUser();
        return paymentResultRepository.getByUser(user).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void updateResults() {
        log.info("Ödəniş nəticələri yenilənir");
        User user = userService.getCurrentUser();
        for (PaymentResult paymentResult : paymentResultRepository.getByUserAndLastCreatedAt(user,
                Instant.now().minusSeconds(7 * 24 * 60 * 60 * 1000)))
            paymentService.updateResults(paymentResult.getInvoiceUuid());
        log.info("Ödəniş nəticələri yeniləndi");
    }

    private PaymentResultResponse toResponse(PaymentResult paymentResult) {
        return new PaymentResultResponse(
                paymentResult.getStatus(),
                paymentResult.getAmount(),
                paymentResult.getCurrency(),
                paymentResult.getDescription(),
                paymentResult.getPaymentCreateDate());
    }
}
