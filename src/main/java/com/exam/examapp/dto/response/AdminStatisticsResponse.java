package com.exam.examapp.dto.response;

import com.exam.examapp.model.Log;

import java.util.List;

public record AdminStatisticsResponse(
        int totalUser,
        int percentageUserIncrease,
        double thisMonthProfit,
        double differenceWithLastMonthProfit,
        long totalExam,
        int thisMonthCreatedExam,
        List<Log> logs
) {
}
