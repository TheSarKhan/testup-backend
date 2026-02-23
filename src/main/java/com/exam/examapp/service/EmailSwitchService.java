package com.exam.examapp.service;

import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.EmailSwitch;
import com.exam.examapp.model.enums.Email;
import com.exam.examapp.repository.EmailSwitchRepository;
import com.exam.examapp.service.interfaces.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSwitchService {
    private final EmailSwitchRepository repository;

    private final LogService logService;

    public Email getCurrentEmailService() {
        return repository.get().orElseThrow(() ->
                new ResourceNotFoundException("Email service not found")).getEmail();
    }

    public void chanceEmailService(Email email) {
        log.info("Changing email service to {}", email);
        EmailSwitch emailSwitch = repository.get().orElseThrow(() ->
                new ResourceNotFoundException("Email service not found"));
        emailSwitch.setEmail(email);
        repository.save(emailSwitch);
        log.info("Email service changed to {}", email);
        logService.save("Email servis deyisdirildi. " + email.name(), null);
    }
}
