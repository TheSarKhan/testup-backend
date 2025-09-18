package com.exam.examapp.dto.request.payment;

import lombok.Data;

@Data
public class PaymentInitRequest{
    private String merchant;
    private Body body;
}