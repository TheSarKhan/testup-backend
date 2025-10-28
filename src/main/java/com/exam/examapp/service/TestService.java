package com.exam.examapp.service;

import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.dto.response.LogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestService {
    private final GraphService graphService;

    public AdminStatisticsResponse getAdminStatistics() {
        long totalUsers = 123321123;
        long newUserCount = 321123;
        long lastMonthUserCount = 123;

        int percentageUserIncrease = (int) ((newUserCount * 100.0 / lastMonthUserCount) - 100);

        double totalAmount = 987789987;

        double lastMonthAmount = 789987;

        long totalExams = 567765;
        long thisMonthCreatedExam = 765;

        List<LogResponse> logs = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            logs.add(new LogResponse(
                    UUID.randomUUID(),
                    "This is test log" + i,
                    UUID.randomUUID(),
                    "This is log test user" + i,
                    null,
                    "hello" + i + "@gmail.com",
                    null,
                    Instant.now(),
                    null
            ));
        }

        List<List<List<Double>>> listss = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            List<List<Double>> lists = new ArrayList<>();
            List<Double> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(Math.random() * 10000 + 100);
            }
            lists.add(list);
            list = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                list.add(Math.random() * 10000 + 100);
            }
            lists.add(list);
            list = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                list.add(Math.random() * 10000 + 100);
            }
            lists.add(list);
            listss.add(lists);
        }

        return new AdminStatisticsResponse(
                (int) totalUsers,
                percentageUserIncrease,
                totalAmount,
                lastMonthAmount,
                totalExams,
                (int) thisMonthCreatedExam,
                logs,
                graphService.fillGraph(listss.getFirst()),
                graphService.fillGraph(listss.get(1)),
                graphService.fillGraph(listss.get(2))
        );
    }
}
