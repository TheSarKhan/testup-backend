package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ScoreService {
    public static void calculateScore(StudentExam studentExam,
                                      Map<UUID, AnswerStatus> answerStatusMap,
                                      List<Integer> correctAndWrongCounts) {
        log.info("Bal hesablanır");
        double score = 0;
        for (SubjectStructureQuestion subjectStructureQuestion : studentExam.getExam().getSubjectStructureQuestions()) {
            String formula = subjectStructureQuestion.getSubjectStructure().getFormula();

            if (formula != null) {
                String formattedFormula = formatFormulaWithCounts(formula, correctAndWrongCounts);
                score += new ExpressionBuilder(formattedFormula).build().evaluate();
            } else {
                score += calculatePointBasedScore(subjectStructureQuestion, answerStatusMap);
            }
        }
        studentExam.setScore(score);
        log.info("Bal hesablandı");
    }

    private static double calculatePointBasedScore(SubjectStructureQuestion subjectStructureQuestion,
                                                  Map<UUID, AnswerStatus> answerStatusMap) {
        log.info("Bal əsasında hesablanır");
        Map<Integer, Integer> questionToPointMap = subjectStructureQuestion.getSubjectStructure().getQuestionToPointMap();
        List<Question> questions = subjectStructureQuestion.getQuestion();

        double score = 0;
        for (Map.Entry<Integer, Integer> entry : questionToPointMap.entrySet()) {
            Question currentQuestion = questions.get(entry.getKey());
            if (AnswerStatus.CORRECT.equals(answerStatusMap.get(currentQuestion.getId()))) {
                score += entry.getValue();
            }
        }
        log.info("Bal əsasında hesablandı");
        return score;
    }

    private static String formatFormulaWithCounts(
            String formula, List<Integer> correctAndWrongCounts) {
        log.info("Düstur xəritələnir");
        String formattedFormula = formula;
        formattedFormula = formattedFormula.replace("a", String.valueOf(correctAndWrongCounts.get(0)));
        formattedFormula = formattedFormula.replace("b", String.valueOf(correctAndWrongCounts.get(1)));
        formattedFormula = formattedFormula.replace("c", String.valueOf(correctAndWrongCounts.get(2)));
        formattedFormula = formattedFormula.replace("d", String.valueOf(correctAndWrongCounts.get(3)));
        formattedFormula = formattedFormula.replace("e", "0");
        formattedFormula = formattedFormula.replace("f", String.valueOf(correctAndWrongCounts.get(4)));
        formattedFormula = formattedFormula.replace("g", String.valueOf(correctAndWrongCounts.get(5)));
        formattedFormula = formattedFormula.replace("h", String.valueOf(correctAndWrongCounts.get(6)));
        formattedFormula = formattedFormula.replace("i", String.valueOf(correctAndWrongCounts.get(7)));
        formattedFormula = formattedFormula.replace("j", "0");
        log.info("Düstur xəritələndi");
        return formattedFormula;
    }
}
