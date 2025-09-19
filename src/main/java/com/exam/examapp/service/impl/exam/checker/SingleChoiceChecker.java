package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SingleChoiceChecker implements AnswerChecker {
    @Override
    public void check(Question question, QuestionDetails details, String answer, Map<UUID, AnswerStatus> answerStatusMap, List<Integer> counts) {
        if (String.valueOf(details.correctVariants().getFirst()).equals(answer)) {
            answerStatusMap.put(question.getId(), AnswerStatus.CORRECT);
            counts.set(0, counts.getFirst() + 1);
        } else if (answer == null) answerStatusMap.put(question.getId(), AnswerStatus.NOT_ANSWERED);
        else {
            answerStatusMap.put(question.getId(), AnswerStatus.WRONG);
            counts.set(4, counts.get(4) + 1);
        }
    }
}
