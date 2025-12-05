package com.exam.examapp.init;

import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.question.QuestionStorage;
import com.exam.examapp.repository.QuestionStorageRepository;
import com.exam.examapp.service.interfaces.PackService;
import com.exam.examapp.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInitializer {
    private final UserService userService;

    private final QuestionStorageRepository questionStorageRepository;

    private final PackService packService;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.full-name}")
    private String adminFullName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.phone-number}")
    private String adminPhoneNumber;

    @PostConstruct
    public void init() {
        if (userService.getUsersByRole(Role.ADMIN).isEmpty()) {
            User admin = User.builder()
                    .fullName(adminFullName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .phoneNumber(adminPhoneNumber)
                    .role(Role.ADMIN)
                    .pack(packService.getPackByName("Admin"))
                    .info(new TeacherInfo(Instant.now(), 0, 0, new HashMap<>()))
                    .isAcceptedTerms(true)
                    .build();

            questionStorageRepository.save(QuestionStorage.builder().teacher(admin).build());

            admin = userService.save(admin);

            log.info("Admin created :{}", admin);
        }
    }
}
