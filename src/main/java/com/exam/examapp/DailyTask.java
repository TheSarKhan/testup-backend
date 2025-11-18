package com.exam.examapp;

import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.model.User;
import com.exam.examapp.repository.UserRepository;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.PackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyTask {
    private static final String EMAIL_SUBJECT = "Exam App Paket Xatirladici";

    private static final String EMAIL_BODY = "Salam sizin paketin vaxti %s tarixidir eger odenisi etmeseniz paketiniz legv olunacaq.";

    private final UserRepository userRepository;

    private final PackService packService;

    private final NotificationService notificationService;

    private final EmailService emailService;

    private final LogService logService;

    @Value("${app.default-pack-name}")
    private String defaultPackName;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTeacherInfo() {
        log.info("Müəllim məlumatlarını sıfırlayır");
        List<User> teachers = userRepository.getTeachersByLastMonth();
        teachers.forEach(
                teacher -> {
                    teacher.getInfo().setThisMonthStartTime(Instant.now());
                    teacher.getInfo().setThisMonthCreatedExamCount(0);
                    userRepository.save(teacher);
                });
        String message = "Müəllim məlumatlarını sıfırlandı. Müəllim sayı: " + teachers.size();
        log.info(message);
        logService.save(message, null);
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPaymentReminders() {
        List<User> teachers = userRepository.getTeachersByPackExceptDefault(defaultPackName);

        for (User teacher : teachers) {
            handleExpiredPack(teacher);
            handlePaymentReminders(teacher);
        }
    }

    private void handleExpiredPack(User teacher) {
        if (teacher.getNextPaymentDate().isAfter(Instant.now().plusSeconds(10 * 24 * 60 * 60))) {
            log.info("{} paketi bitib.", teacher.getEmail());
            logService.save(teacher.getEmail() + " paketi bitib.", null);
            teacher.setPack(packService.getPackByName(defaultPackName));
            teacher.setNextPaymentDate(null);
            userRepository.save(teacher);
            log.info("{} paketi silindi", teacher.getEmail());
            logService.save(teacher.getEmail() + " paketi silindi", null);
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
        log.info("{} paketninin vaxtı xatırladılır.", teacher.getEmail());
        String message = EMAIL_BODY.formatted(teacher.getNextPaymentDate().toString());
        emailService.sendEmail(teacher.getEmail(), EMAIL_SUBJECT, message);
        notificationService.sendNotification(
                new NotificationRequest(EMAIL_SUBJECT, message, teacher.getEmail())
        );
        log.info("{} paketninin vaxtı xatırladıldı.", teacher.getEmail());
        logService.save(teacher.getEmail() + " paketninin vaxtı xatırladıldı.", null);
    }
}
