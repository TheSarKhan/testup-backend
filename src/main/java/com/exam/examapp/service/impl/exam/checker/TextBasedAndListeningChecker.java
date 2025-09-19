package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TextBasedAndListeningChecker(AnswerCheckerFactory factory) implements AnswerChecker {
    @Override
    public void check(Question question, QuestionDetails details, String answer, Map<UUID, AnswerStatus> answerStatusMap, List<Integer> counts) {
        if (question.getQuestions() == null) {
            return;
        }

        for (Question questionQuestion : question.getQuestions()) {
            QuestionDetails questionDetails = questionQuestion.getQuestionDetails();
            AnswerChecker answerChecker = factory.getChecker(questionQuestion.getType());
            answerChecker.check(questionQuestion, questionDetails, answer, answerStatusMap, counts);
        }
    }
}
