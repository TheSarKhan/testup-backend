package com.exam.examapp.service.impl.exam.checker;

import com.exam.examapp.model.enums.QuestionType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AnswerCheckerFactory {
    private final Map<QuestionType, AnswerChecker> checkerMap = new HashMap<>();

    public AnswerCheckerFactory(List<AnswerChecker> checkers) {
        for (AnswerChecker checker : checkers) {
            if (checker instanceof SingleChoiceChecker) {
                checkerMap.put(QuestionType.SINGLE_CHOICE, checker);
            } else if (checker instanceof MultiChoiceChecker) {
                checkerMap.put(QuestionType.MULTI_CHOICE, checker);
            }else if (checker instanceof MatchChecker) {
                checkerMap.put(QuestionType.MATCH, checker);
            } else if (checker instanceof OpenEndedChecker) {
                checkerMap.put(QuestionType.OPEN_ENDED, checker);
            } else if (checker instanceof TextBasedAndListeningChecker) {
                checkerMap.put(QuestionType.LISTENING, checker);
                checkerMap.put(QuestionType.TEXT_BASED, checker);
            }
        }
    }

    public AnswerChecker getChecker(QuestionType type) {
        return checkerMap.get(type);
    }
}