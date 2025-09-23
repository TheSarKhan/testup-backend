package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.repository.StudentExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final AnswerService answerService;

    private final StudentExamRepository studentExamRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public void calculateResult(StudentExam studentExam) {
        List<Integer> correctAndWrongCounts = new ArrayList<>(List.of(0, 0, 0, 0, 0, 0, 0, 0));

        Map<UUID, AnswerStatus> answerStatusMap = answerService.checkAnswers(studentExam, correctAndWrongCounts);

        answerService.handleUncheckedQuestions(studentExam, answerStatusMap);

        ScoreService.calculateScore(studentExam, answerStatusMap, correctAndWrongCounts);

        updateStatistics(studentExam, answerStatusMap, correctAndWrongCounts);

        studentExamRepository.save(studentExam);
    }

    private void updateStatistics(StudentExam studentExam,
                                  Map<UUID, AnswerStatus> answerStatusMap,
                                  List<Integer> correctAndWrongCounts) {
        int correctCount = correctAndWrongCounts.stream().limit(4).mapToInt(Integer::intValue).sum();
        int wrongCount = correctAndWrongCounts.stream().skip(4).mapToInt(Integer::intValue).sum();

        studentExam.setNumberOfCorrectAnswers(correctCount);
        studentExam.setNumberOfWrongAnswers(wrongCount);

        studentExam.setNumberOfNotAnsweredQuestions(
                studentExam.getNumberOfQuestions()
                        - (correctCount + wrongCount + studentExam.getNumberOfNotCheckedYetQuestions())
        );

        Map<String, Map<Integer, String>> subjectToAnswersMap =
                answerService.mapStudentDataToSubjects(studentExam, studentExam.getQuestionIdToAnswerMap());
        studentExam.setSubjectToQuestionToAnswer(subjectToAnswersMap);

        Map<String, Map<Integer, AnswerStatus>> subjectToQuestionAnswerStatusMap =
                answerService.mapStudentDataToSubjects(studentExam, answerStatusMap);
        studentExam.setSubjectToQuestionToAnswerStatus(subjectToQuestionAnswerStatusMap);
    }

    public ResultStatisticResponse getResultStatisticResponse(UUID studentExamId, StudentExam studentExam) {
        long secondsPassed = studentExam.getEndTime().getEpochSecond() - studentExam.getStartTime().getEpochSecond();

        ExamResponse examResponse = ExamMapper.toResponse(studentExam.getExam());

        String shareLink = baseUrl + "/api/v1/exam/result?studentExamId=" + studentExamId;

        return new ResultStatisticResponse(
                studentExam.getNumberOfCorrectAnswers(),
                studentExam.getNumberOfWrongAnswers(),
                studentExam.getNumberOfNotAnsweredQuestions(),
                studentExam.getNumberOfNotCheckedYetQuestions(),
                studentExam.getNumberOfQuestions(),
                studentExam.getExam().getDurationInSeconds(),
                (int) secondsPassed,
                studentExam.getExamRating(),
                examResponse,
                studentExam.getSubjectToQuestionToAnswer(),
                studentExam.getSubjectToQuestionToAnswerStatus(),
                shareLink,
                studentExam.getExam().getExplanationVideoUrl()
        );
    }
}
