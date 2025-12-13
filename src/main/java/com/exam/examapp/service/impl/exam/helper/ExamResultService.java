package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.repository.StudentExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final AnswerService answerService;

    private final StudentExamRepository studentExamRepository;

    private final ExamMapper examMapper;

    @Value("${app.front-base-url}")
    private String frontBaseUrl;

    public void calculateResult(StudentExam studentExam) {
        log.info("İmtahan nəticələri hesablanır");
        List<Integer> correctAndWrongCounts = new ArrayList<>(List.of(0, 0, 0, 0, 0, 0, 0, 0));

        Map<UUID, AnswerStatus> answerStatusMap = answerService.checkAnswers(studentExam, correctAndWrongCounts);

        answerService.handleUncheckedQuestions(studentExam, answerStatusMap);

        ScoreService.calculateScore(studentExam, answerStatusMap, correctAndWrongCounts);

        updateStatistics(studentExam, answerStatusMap, correctAndWrongCounts);

        studentExamRepository.save(studentExam);
        log.info("İmtahan nəticələri hesablandı");
    }

    private void updateStatistics(StudentExam studentExam,
                                  Map<UUID, AnswerStatus> answerStatusMap,
                                  List<Integer> correctAndWrongCounts) {
        log.info("Statistika yenilənir");
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

        studentExam.setQuestionIdToAnswerStatusMap(answerStatusMap);
        log.info("Statistika yeniləndi");
    }

    public ResultStatisticResponse getResultStatisticResponse(UUID studentExamId, StudentExam studentExam) {
        log.info("Imtahan statistikası hazırlanır");
        long secondsPassed = studentExam.getEndTime().getEpochSecond() - studentExam.getStartTime().getEpochSecond();

        ExamResponse examResponse = examMapper.toResponse(studentExam.getExam());

        String shareLink = frontBaseUrl + "/student-exam-result?studentExamId=" + studentExamId;

        ResultStatisticResponse resultStatisticResponse = new ResultStatisticResponse(
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
        log.info("Imtahan statistikası hazırlandı");
        return resultStatisticResponse;
    }
}
