package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiChoiceChecker implements AnswerChecker{
    @Override
    public void check(Question question, QuestionDetails details, String answer, Map<UUID, AnswerStatus> answerStatusMap, List<Integer> counts) {
        StringBuilder sb = new StringBuilder();
        for (Character correctVariant : details.correctVariants()) {
            sb.append(correctVariant);
        }
        if (sb.toString().equals(answer)) {
            answerStatusMap.put(question.getId(), AnswerStatus.CORRECT);
            counts.set(1, counts.get(1) + 1);

        } else if (answer == null) answerStatusMap.put(question.getId(), AnswerStatus.NOT_ANSWERED);
        else {
            answerStatusMap.put(question.getId(), AnswerStatus.WRONG);
            counts.set(5, counts.get(5) + 1);
        }
    }
}
