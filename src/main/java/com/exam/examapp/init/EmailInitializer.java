package com.exam.examapp.init;

import com.exam.examapp.model.EmailSwitch;
import com.exam.examapp.model.enums.Email;
import com.exam.examapp.repository.EmailSwitchRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailInitializer {
    private final EmailSwitchRepository repository;

    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            repository.save(EmailSwitch.builder().email(Email.GOOGLE).build());
            log.info("Email service initialized.");
        }
    }
}
