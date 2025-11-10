package com.exam.examapp.dto.response;

import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.model.enums.AnswerStatus;

import java.util.Map;

public record ResultStatisticResponse(
        Integer totalCorrectAnswers,
        Integer totalWrongAnswers,
        Integer totalNoAnsweredQuestions,
        Integer totalUncheckedQuestions,
        Integer totalQuestionCount,
        Integer examDurationInSeconds,
        Integer youFinishesInSeconds,
        Double rating,
        ExamResponse examResponse,
        Map<String, Map<Integer, String>> subjectToQuestionToAnswer,
        Map<String, Map<Integer, AnswerStatus>> subjectToQuestionToAnswerStatus,
        String shareUrl,
        String explanationVideoUrl
) {
}
