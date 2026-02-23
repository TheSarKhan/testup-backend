package com.exam.examapp.security.service.impl;

import com.exam.examapp.dto.request.MultiEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailServiceImpl {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    String from;

    public String sendEmail(String to, String subject, String content) {
        log.info("Email göndərilir");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
        log.info("Email uğurla göndərildi");
        return "Email uğurla göndərildi";
    }

    public void sendEmailToAll(MultiEmailRequest request) {
        request.emails().forEach(email -> sendEmail(email, request.title(), request.message()));
    }
}
