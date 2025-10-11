package com.exam.examapp.dto.response;

import java.util.List;

public record AdminStatisticsResponse(
        int totalUser,
        int percentageUserIncrease,
        double thisMonthProfit,
        double differenceWithLastMonthProfit,
        long totalExam,
        int thisMonthCreatedExam,
        List<LogResponse> logs,
        GraphResponse profitGraph,
        GraphResponse teacherRegisterGraph,
        GraphResponse studentRegisterGraph
) {
}
