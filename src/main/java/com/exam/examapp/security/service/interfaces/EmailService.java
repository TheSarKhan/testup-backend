package com.exam.examapp.security.service.interfaces;

import com.exam.examapp.dto.request.MultiEmailRequest;

public interface EmailService {
    String sendEmail(String to, String subject, String content);

    void sendEmailToAll(MultiEmailRequest request);
}
