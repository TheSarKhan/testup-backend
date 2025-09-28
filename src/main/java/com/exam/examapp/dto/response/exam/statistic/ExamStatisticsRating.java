package com.exam.examapp.dto.response.exam.statistic;

public record ExamStatisticsRating(
        double rating,
        long totalRatingCount,
        long totaFiveStar,
        long totalForStar,
        long totalThreeStar,
        long totalTwoStar,
        long totalOneStar
) {
}
