package com.exam.examapp.security.service.impl;

import com.exam.examapp.AppMessage;
import com.exam.examapp.security.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Override
    public String sendEmail(String to, String subject, String content) {
        log.info(AppMessage.SEND_EMAIL.format(to, subject, content));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
        log.info(AppMessage.EMAIL_SENT_SUCCESS.getMessage());
        return AppMessage.EMAIL_SENT_SUCCESS.getMessage();
    }
}
