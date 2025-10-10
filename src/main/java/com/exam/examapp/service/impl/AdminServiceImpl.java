package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.model.Log;
import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.repository.PaymentResultRepository;
import com.exam.examapp.repository.UserRepository;
import com.exam.examapp.service.interfaces.AdminService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserService userService;

    private final UserRepository userRepository;

    private final PaymentResultRepository paymentResultRepository;

    private final ExamRepository examRepository;

    private final LogService logService;

    @Override
    public void changeUserRoleViaEmail(String email, Role role) {
        log.info("E-poçt ünvanı {} olan istifadəçinin rolu {} olaraq dəyişdirilir", email, role);
        User user = userService.getByEmail(email);
        changeUserRole(user, role);
        log.info("E-poçt ünvanı {} olan istifadəçinin rolu {} olaraq dəyişdirildi", email, role);
        logService.save("E-poçt ünvanı " + email + " olan istifadəçinin rolu " + role + " olaraq dəyişdirildi",
                userService.getCurrentUserOrNull());
    }

    @Override
    public void changeUserRoleViaId(UUID id, Role role) {
        log.info("Id-si {} olan istifadəçinin rolu {} olaraq dəyişdirilir", id, role);
        User user = userService.getUserById(id);
        changeUserRole(user, role);
        log.info("Id-si {} olan istifadəçinin rolu {} olaraq dəyişdirildi", id, role);
        logService.save("Id-si " + id + " olan istifadəçinin rolu " + role + " olaraq dəyişdirildi",
                userService.getCurrentUserOrNull());
    }

    @Override
    public AdminStatisticsResponse getAdminStatistics() {
        Instant now = Instant.now();
        Instant oneMonthAgo = now.minusSeconds(60L * 60 * 24 * 30);
        Instant twoMonthsAgo = now.minusSeconds(60L * 60 * 24 * 30 * 2);

        long totalUsers = userRepository.count();
        long newUserCount = userRepository.countByCreatedAtAfter(oneMonthAgo);
        long lastMonthUserCount = userRepository.countByCreatedAtBetween(twoMonthsAgo, oneMonthAgo);

        int percentageUserIncrease = 0;
        if (lastMonthUserCount > 0) {
            percentageUserIncrease = (int) ((newUserCount * 100.0 / lastMonthUserCount) - 100);
        }

        double totalAmount = paymentResultRepository.getByStatus("APPROVED")
                .stream()
                .mapToDouble(PaymentResult::getAmount)
                .sum();

        double lastMonthAmount = paymentResultRepository
                .getByStatusAndCreatedAtAfter("APPROVED", oneMonthAgo)
                .stream()
                .mapToDouble(PaymentResult::getAmount)
                .sum();

        long totalExams = examRepository.count();
        long thisMonthCreatedExam = examRepository.countByCreatedAtAfter(oneMonthAgo);

        List<Log> logs = logService.getAllOrderByCreatedAt(1, 5);

        return new AdminStatisticsResponse(
                (int) totalUsers,
                percentageUserIncrease,
                totalAmount,
                lastMonthAmount,
                totalExams,
                (int) thisMonthCreatedExam,
                logs
        );
    }

    private void changeUserRole(User user, Role role) {
        user.setRole(role);
        userService.save(user);
    }
}
