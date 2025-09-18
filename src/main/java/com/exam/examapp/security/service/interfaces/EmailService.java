package com.exam.examapp.security.service.interfaces;

public interface EmailService {
    String sendEmail(String to, String subject, String content);
}
