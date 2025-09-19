package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OpenEndedChecker implements AnswerChecker{
    @Override
    public void check(Question question, QuestionDetails details, String answer, Map<UUID, AnswerStatus> answerStatusMap, List<Integer> counts) {
        if (details.isAuto()) {
            if (details.answer().equals(answer))
                answerStatusMap.put(question.getId(), AnswerStatus.CORRECT);
            else if (answer == null) {
                answerStatusMap.put(question.getId(), AnswerStatus.NOT_ANSWERED);
                counts.set(3, counts.get(3) + 1);
            } else {
                answerStatusMap.put(question.getId(), AnswerStatus.WRONG);
                counts.set(7, counts.get(7) + 1);
            }
        } else answerStatusMap.put(question.getId(), AnswerStatus.WAITING_FOR_REVIEW);
    }
}
