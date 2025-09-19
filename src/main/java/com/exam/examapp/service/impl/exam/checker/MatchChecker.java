package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchChecker implements AnswerChecker{
    @Override
    public void check(Question question, QuestionDetails details, String answer, Map<UUID, AnswerStatus> answerStatusMap, List<Integer> counts) {
        Map<Character, List<Character>> characterListMap =
                details.numberToCorrectVariantsMap();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, List<Character>> characterListEntry :
                characterListMap.entrySet()) {
            sb.append(characterListEntry.getKey());
            characterListEntry.getValue().forEach(sb::append);
        }
        if (sb.toString().equals(answer)) {
            answerStatusMap.put(question.getId(), AnswerStatus.CORRECT);
            counts.set(2, counts.get(2) + 1);

        } else if (answer == null) answerStatusMap.put(question.getId(), AnswerStatus.NOT_ANSWERED);
        else {
            answerStatusMap.put(question.getId(), AnswerStatus.WRONG);
            counts.set(6, counts.get(6) + 1);
        }
    }
}
