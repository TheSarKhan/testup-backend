package com.exam.examapp;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.PaymentResultRepository;
import com.exam.examapp.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyTask {
  private final UserRepository userRepository;

  private final PaymentResultRepository paymentResultRepository;

  @Scheduled(cron = "0 0 2 * * ?")
  public void runEveryEvening() {
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
    public void runEveryMorning() {
        List<User> teachers = userRepository.getByRole(Role.TEACHER);
        for (User teacher : teachers) {

        }
    }
}
