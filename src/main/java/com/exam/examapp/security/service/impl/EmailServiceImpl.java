package com.exam.examapp.security.service.impl;

import com.exam.examapp.dto.request.MultiEmailRequest;
import com.exam.examapp.model.enums.Email;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.EmailSwitchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailSwitchService emailSwitchService;

    private final SmtpEmailServiceImpl smtpEmailService;

    private final SendPulseServiceImpl sendPlusService;

    @Override
    public String sendEmail(String to, String subject, String content) {
        Email email = emailSwitchService.getCurrentEmailService();
        if (Email.GOOGLE.equals(email)) {
            return smtpEmailService.sendEmail(to, subject, content);
        } else if (Email.SEND_PLUS.equals(email)) {
            return sendPlusService.sendEmail(to, subject, content);
        }
        throw new RuntimeException("Email service not found");
    }

    @Override
    public void sendEmailToAll(MultiEmailRequest request) {
        Email email = emailSwitchService.getCurrentEmailService();
        if (Email.GOOGLE.equals(email)) {
            smtpEmailService.sendEmailToAll(request);
            return;
        } else if (Email.SEND_PLUS.equals(email)) {
            sendPlusService.sendEmailToAll(request);
            return;
        }
        throw new RuntimeException("Email service not found");
    }
}
