package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.dto.response.exam.statistic.ExamStatistics;
import com.exam.examapp.dto.response.exam.statistic.ExamStatisticsBestStudent;
import com.exam.examapp.dto.response.exam.statistic.ExamStatisticsRating;
import com.exam.examapp.dto.response.exam.statistic.ExamStatisticsStudent;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.impl.user.UserServiceImpl;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.exam.ExamCheckService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamCheckServiceImpl implements ExamCheckService {
    private static final String MAIL_SUBJECT = "Sualiniz Yoxlandi";

    private static final String MAIL_BODY = "Muellim sizin sualiniz yoxladi. Imtahan adi: %s";

    private final StudentExamRepository studentExamRepository;

    private final NotificationService notificationService;

    private final EmailService emailService;

    private final LogService logService;

    private final UserServiceImpl userService;

    private final ExamMapper examMapper;

    private static void getAnswerList(List<Question> questions, Map<UUID, AnswerStatus> questionIdToAnswerStatusMap, List<Integer> list) {
        log.info("Cavab listi götürülür");
        for (Question question : questions) {
            if (QuestionType.TEXT_BASED.equals(question.getType()) || QuestionType.LISTENING.equals(question.getType())) {
                getAnswerList(question.getQuestions(), questionIdToAnswerStatusMap, list);
                continue;
            }
            AnswerStatus answerStatus = questionIdToAnswerStatusMap.get(question.getId());
            switch (answerStatus) {
                case AnswerStatus.CORRECT -> {
                    switch (question.getType()) {
                        case QuestionType.SINGLE_CHOICE -> list.set(0, list.getFirst() + 1);
                        case QuestionType.MULTI_CHOICE -> list.set(1, list.get(1) + 1);
                        case QuestionType.MATCH -> list.set(2, list.get(2) + 1);
                        case QuestionType.OPEN_ENDED -> {
                            if (!question.getQuestionDetails().isAuto())
                                list.set(3, list.get(3) + 1);
                            else list.set(4, list.get(4) + 1);
                        }
                    }
                }
                case AnswerStatus.WRONG -> {
                    switch (question.getType()) {
                        case QuestionType.SINGLE_CHOICE -> list.set(5, list.get(5) + 1);
                        case QuestionType.MULTI_CHOICE -> list.set(6, list.get(6) + 1);
                        case QuestionType.MATCH -> list.set(7, list.get(7) + 1);
                        case QuestionType.OPEN_ENDED -> {
                            if (!question.getQuestionDetails().isAuto())
                                list.set(8, list.get(8) + 1);
                            else list.set(9, list.get(9) + 1);
                        }
                    }
                }
            }
        }
    }

    private static double incrementScore(List<Question> questions, Map<UUID, AnswerStatus> questionIdToAnswerStatusMap, double score, Map<Integer, Integer> questionToPointMap) {
        log.info("Bal artırılır. Bal:{}", score);
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (QuestionType.TEXT_BASED.equals(question.getType()) || QuestionType.LISTENING.equals(question.getType())) {
                score = incrementScore(question.getQuestions(), questionIdToAnswerStatusMap, score, questionToPointMap);
                continue;
            }
            AnswerStatus answerStatus = questionIdToAnswerStatusMap.get(question.getId());
            if (AnswerStatus.CORRECT.equals(answerStatus)) {
                score += questionToPointMap.get(i + 1);
            }
        }
        return score;
    }

    private static String formatFormulaWithCounts(
            String formula, List<Integer> correctAndWrongCounts) {
        log.info("Formula hazırlanır");
        String formattedFormula = formula;
        formattedFormula = formattedFormula.replace("a", String.valueOf(correctAndWrongCounts.get(0)));
        formattedFormula = formattedFormula.replace("b", String.valueOf(correctAndWrongCounts.get(1)));
        formattedFormula = formattedFormula.replace("c", String.valueOf(correctAndWrongCounts.get(2)));
        formattedFormula = formattedFormula.replace("d", String.valueOf(correctAndWrongCounts.get(3)));
        formattedFormula = formattedFormula.replace("e", String.valueOf(correctAndWrongCounts.get(4)));
        formattedFormula = formattedFormula.replace("f", String.valueOf(correctAndWrongCounts.get(5)));
        formattedFormula = formattedFormula.replace("g", String.valueOf(correctAndWrongCounts.get(6)));
        formattedFormula = formattedFormula.replace("h", String.valueOf(correctAndWrongCounts.get(7)));
        formattedFormula = formattedFormula.replace("i", String.valueOf(correctAndWrongCounts.get(8)));
        formattedFormula = formattedFormula.replace("j", String.valueOf(correctAndWrongCounts.get(9)));
        return formattedFormula;
    }

    private StudentExam getStudentExam(UUID studentExamId) {
        return studentExamRepository.findById(studentExamId).orElseThrow(() ->
                new ResourceNotFoundException("Tələbə imtahanı tapılmadı"));
    }

    private List<ExamStatisticsStudent> getExamStudents(List<StudentExam> studentExams) {
        log.info("Tələbələr hazırlanır");
        List<ExamStatisticsStudent> list = studentExams.stream()
                .filter(studentExam -> ExamStatus.COMPLETED.equals(studentExam.getStatus()) ||
                        ExamStatus.EXPIRED.equals(studentExam.getStatus()))
                .sorted(Comparator.comparing(StudentExam::getEndTime).reversed())
                .map(examStudent -> {
                    Instant endTime = examStudent.getEndTime();
                    long durationInSecond = endTime.getEpochSecond() -
                            examStudent.getStartTime().getEpochSecond();
                    durationInSecond = examStudent.getExam().getDurationInSeconds() >
                            durationInSecond ?
                            examStudent.getExam().getDurationInSeconds() :
                            durationInSecond;
                    return new ExamStatisticsStudent(
                            examStudent.getExam().getId(),
                            examStudent.getId(),
                            examStudent.getStudent() == null ?
                                    examStudent.getStudentName() :
                                    examStudent.getStudent().getFullName(),
                            durationInSecond,
                            examStudent.getScore(),
                            examStudent.getNumberOfQuestions(),
                            examStudent.getNumberOfCorrectAnswers(),
                            examStudent.getNumberOfNotCheckedYetQuestions() > 0);
                })
                .toList();
        log.info("Tələbələr hazırlandı");
        return list;
    }

    private ExamStatisticsRating getExamStatisticsRating(Exam exam) {
        log.info("İmtahan statistikası götürülür");
        Map<UUID, Integer> userIdToRatingMap = exam.getUserIdToRatingMap();
        Set<Map.Entry<UUID, Integer>> ratingEntries = userIdToRatingMap.entrySet();
        long totalFiveStarCount = ratingEntries.stream()
                .filter(e -> e.getValue() == 5).count();
        long totalForStarCount = ratingEntries.stream()
                .filter(e -> e.getValue() == 4).count();
        long totalThreeStarCount = ratingEntries.stream()
                .filter(e -> e.getValue() == 3).count();
        long totalTwoStarCount = ratingEntries.stream()
                .filter(e -> e.getValue() == 2).count();
        long totalOneStarCount = ratingEntries.stream()
                .filter(e -> e.getValue() == 1).count();

        log.info("İmtahan statistikası götürüldü");
        return new ExamStatisticsRating(
                exam.getRating(),
                userIdToRatingMap.size(),
                totalFiveStarCount,
                totalForStarCount,
                totalThreeStarCount,
                totalTwoStarCount,
                totalOneStarCount
        );
    }

    private List<ExamStatisticsBestStudent> getBestStudents(List<StudentExam> studentExams) {
        log.info("Ən yaxşı tələbələr hazırlanır");
        List<ExamStatisticsBestStudent> list = studentExams.stream()
                .filter(studentExam -> ExamStatus.COMPLETED.equals(studentExam.getStatus()) ||
                        ExamStatus.EXPIRED.equals(studentExam.getStatus()))
                .sorted(Comparator.comparing(StudentExam::getScore))
                .limit(5)
                .map(examStudent -> {
                    long durationInSecond = examStudent.getEndTime().getEpochSecond() -
                            examStudent.getStartTime().getEpochSecond();
                    durationInSecond = examStudent.getExam().getDurationInSeconds() >
                            durationInSecond ?
                            examStudent.getExam().getDurationInSeconds() :
                            durationInSecond;
                    return new ExamStatisticsBestStudent(
                            examStudent.getExam().getId(),
                            examStudent.getId(),
                            examStudent.getStudent() == null ?
                                    examStudent.getStudentName() :
                                    examStudent.getStudent().getFullName(),
                            durationInSecond,
                            examStudent.getScore());
                })
                .toList();
        log.info("Ən yaxşı tələbələr hazırlandı");
        return list;
    }

    @Override
    @Transactional
    public ExamStatistics getExamStatistics(UUID id) {
        log.info("İmtahan statistikası hazırlanır: {}", id);
        List<StudentExam> studentExams = studentExamRepository.getByExam_Id(id);

        Exam exam = studentExams.getFirst().getExam();

        ExamStatistics examStatistics = new ExamStatistics(
                getExamStatisticsRating(exam),
                getBestStudents(studentExams),
                getExamStudents(studentExams),
                exam.getTeacher().getInfo().getExamToStudentCountMap().get(exam.getId()),
                exam.getTeacher().getPack().getStudentPerExam());
        log.info("İmtahan statistikası: {}", examStatistics);
        return examStatistics;
    }

    @Override
    public StartExamResponse getUserExam(UUID studentExamId) {
        log.info("İmtahan götürülür tələbə imtahan id-si: {}", studentExamId);
        StudentExam studentExam = getStudentExam(studentExamId);

        return new StartExamResponse(
                studentExamId,
                studentExam.getStatus(),
                studentExam.getQuestionIdToAnswerMap(),
                studentExam.getListeningIdToPlayTimeMap(),
                studentExam.getStartTime(),
                examMapper.toResponse(studentExam.getExam())
        );
    }

    @Override
    public void checkAnswer(UUID studentExamId, UUID questionId, AnswerStatus status) {
        log.info("Sual yoxlanılır: {}, Sual id-si: {}, Status: {}", studentExamId, questionId, status);
        StudentExam studentExam = getStudentExam(studentExamId);

        AnswerStatus answerStatus = studentExam.getQuestionIdToAnswerStatusMap().get(questionId);
        if (answerStatus != AnswerStatus.WAITING_FOR_REVIEW)
            throw new BadRequestException("Sual artıq yoxlanılmışdır");

        studentExam.getQuestionIdToAnswerStatusMap().remove(questionId);
        studentExam.getQuestionIdToAnswerStatusMap().put(questionId, answerStatus);

        if (AnswerStatus.NOT_ANSWERED.equals(status)) return;

        studentExam.setScore(calculateScore(studentExam));
        studentExamRepository.save(studentExam);
        log.info("Imtahan yoxlanıldı");
        sendMailAndNotification(studentExam);
        logService.save("Imtahan yoxlanıldı", userService.getCurrentUserOrNull());
    }

    private void sendMailAndNotification(StudentExam studentExam) {
        log.info("Email və Bildiriş göndərilir");
        String examTitle = studentExam.getExam().getExamTitle();
        User student = studentExam.getStudent();
        String emailContent = String.format(MAIL_BODY, examTitle);
        emailService.sendEmail(student.getEmail(), MAIL_SUBJECT, emailContent);
        notificationService.sendNotification(new NotificationRequest(MAIL_SUBJECT, emailContent, student.getEmail()));
    }

    private double calculateScore(StudentExam studentExam) {
        log.info("İmtahan balı hesablanır");
        List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();
        Map<UUID, AnswerStatus> questionIdToAnswerStatusMap = studentExam.getQuestionIdToAnswerStatusMap();

        double score = 0;
        for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
            String formula = subjectStructureQuestion.getSubjectStructure().getFormula();
            List<Question> questions = subjectStructureQuestion.getQuestion();
            if (formula == null) {
                Map<Integer, Integer> questionToPointMap = subjectStructureQuestion
                        .getSubjectStructure().getQuestionToPointMap();

                score = incrementScore(questions, questionIdToAnswerStatusMap, score, questionToPointMap);
            } else {
                List<Integer> list = new ArrayList<>(List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                getAnswerList(questions, questionIdToAnswerStatusMap, list);
                String formatted = formatFormulaWithCounts(formula, list);
                score += new ExpressionBuilder(formatted).build().evaluate();
            }
        }
        log.info("İmtahan balı hesablanılır");
        return score;
    }
}
