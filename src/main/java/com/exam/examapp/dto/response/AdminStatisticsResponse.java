package com.exam.examapp.dto.response;

public record AdminStatisticsResponse(
        int totalUser,
        int percentageUserIncrease,
        double thisMonthProfit,
        double differenceWithLastMonthProfit,
        long totalExam,
        int thisMonthCreatedExam
) {
}
