package com.exam.examapp.dto.response.exam.statistic;

import java.util.List;

public record ExamStatistics(
        ExamStatisticsRating rating,
        List<ExamStatisticsBestStudent> bestStudents,
        List<ExamStatisticsStudent> students,
        int participatedStudentCount,
        int maxAvailableParticipantCount
) {
}
