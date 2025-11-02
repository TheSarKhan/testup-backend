package com.exam.examapp;

import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.UserRepository;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyTask {
    private static final String EMAIL_SUBJECT = "Exam App Paket Xatirladici";

    private static final String EMAIL_BODY = "Salam sizin paketin vaxti %s tarixidir eger odenisi etmeseniz paketiniz legv olunacaq.";

    private final UserRepository userRepository;

    private final PackService packService;

    private final NotificationService notificationService;

    private final EmailService emailService;

    @Value("${app.default-pack-name}")
    private String defaultPackName;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTeacherInfo() {
        List<User> teachers = userRepository.getByRole(Role.TEACHER);
        teachers.stream()
                .filter(
                        teacher ->
                                teacher
                                        .getInfo()
                                        .getThisMonthStartTime()
                                        .isBefore(Instant.now().minusSeconds(2592000)))
                .forEach(
                        teacher -> {
                            teacher.getInfo().setThisMonthStartTime(Instant.now());
                            teacher.getInfo().setThisMonthCreatedExamCount(0);
                            userRepository.save(teacher);
                        });
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPaymentReminders() {
        List<User> teachers = getTeachersByPackExceptDefault();

        for (User teacher : teachers) {
            handleExpiredPack(teacher);
            handlePaymentReminders(teacher);
        }
    }

    private List<User> getTeachersByPackExceptDefault() {
        return userRepository.getByRole(Role.TEACHER).stream()
                .filter(teacher -> !defaultPackName.equals(teacher.getPack().getPackName()))
                .filter(teacher -> isWithinRange(
                        teacher.getNextPaymentDate(),
                        Instant.now().minusSeconds(3 * 24 * 60 * 60 - 1),
                        Instant.now().plusSeconds(11 * 24 * 60 * 60 + 1)))
                .toList();
    }

    private void handleExpiredPack(User teacher) {
        if (teacher.getNextPaymentDate().isAfter(Instant.now().plusSeconds(10 * 24 * 60 * 60))) {
            teacher.setPack(packService.getPackByName(defaultPackName));
            teacher.setNextPaymentDate(null);
            userRepository.save(teacher);
        }
    }

    private void handlePaymentReminders(User teacher) {
        Instant nextPaymentDate = teacher.getNextPaymentDate();

        boolean shouldNotify =
                isWithinRange(nextPaymentDate, Instant.now().minusSeconds(9 * 24 * 60 * 60 - 1),
                        Instant.now().plusSeconds(10 * 24 * 60 * 60)) ||

                        isWithinRange(nextPaymentDate, Instant.now().plusSeconds(2 * 24 * 60 * 60 - 1),
                                Instant.now().plusSeconds(3 * 24 * 60 * 60)) ||

                        isWithinRange(nextPaymentDate, Instant.now().minusSeconds(24 * 60 * 60 - 1),
                                Instant.now().plusSeconds(24 * 60 * 60)) ||

                        isWithinRange(nextPaymentDate, Instant.now().minusSeconds(3 * 24 * 60 * 60 - 1),
                                Instant.now().minusSeconds(2 * 24 * 60 * 60));

        if (shouldNotify) {
            sendReminder(teacher);
        }
    }

    private boolean isWithinRange(Instant target, Instant start, Instant end) {
        return target.isAfter(start) && target.isBefore(end);
    }

    private void sendReminder(User teacher) {
        String message = EMAIL_BODY.formatted(teacher.getNextPaymentDate().toString());
        emailService.sendEmail(teacher.getEmail(), EMAIL_SUBJECT, message);
        notificationService.sendNotification(
                new NotificationRequest(EMAIL_SUBJECT, message, teacher.getEmail())
        );
    }
}
