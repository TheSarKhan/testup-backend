package com.exam.examapp.dto.request.payment;

import lombok.Data;

import java.util.Map;

@Data
public class Body {
    private double amount;
    private String approveURL;
    private String cancelURL;
    private String currencyType;
    private String customMessage;
    private String declineURL;
    private String description;
    private String email;
    private String expireDate;
    private String fullName;
    private int installmentPeriod;
    private String installmentProductType;
    private String languageType;
    private String phoneNumber;
    private boolean sendSms;
    private boolean sendWhatsapp;
    private boolean sendEmail;
    private boolean amountDynamic;
    private boolean directPay;
    private Map<String, String> metadata;
}
