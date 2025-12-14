package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.model.exam.Exam;

public class QuestionCountService {
    public static int getQuestionCount(Exam exam) {
        return exam.getSubjectStructureQuestions().stream()
                .map(subjectStructureQuestion -> {
                            return subjectStructureQuestion.getSubjectStructure().getQuestionCount();
                        }
                )
                .mapToInt(Integer::intValue)
                .sum();
    }
}
