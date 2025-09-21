package com.exam.examapp.dto.response;

import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.model.enums.AnswerStatus;

import java.util.Map;

public record ResultStatisticResponse(
        int totalCorrectAnswers,
        int totalWrongAnswers,
        int totalNoAnsweredQuestions,
        int totalUncheckedQuestions,
        int totalQuestionCount,
        int examDurationInSeconds,
        int youFinishesInSeconds,
        double rating,
        ExamResponse examResponse,
        Map<String, Map<Integer, String>> subjectToQuestionToAnswer,
        Map<String, Map<Integer, AnswerStatus>> subjectToQuestionToAnswerStatus,
        String shareUrl,
        String explanationVideoUrl
) {
}
