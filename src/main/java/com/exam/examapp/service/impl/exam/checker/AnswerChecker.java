package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnswerChecker {
    void check(
            Question question,
            QuestionDetails details,
            String answer,
            Map<UUID, AnswerStatus> answerStatusMap,
            List<Integer> counts
    );
}