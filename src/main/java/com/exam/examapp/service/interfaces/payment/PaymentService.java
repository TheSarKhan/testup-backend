package com.exam.examapp.service.interfaces.payment;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface PaymentService {
    String initPayment(double amount, UUID productId) throws JsonProcessingException, NoSuchAlgorithmException;

    void updateResults(String uuid);
}