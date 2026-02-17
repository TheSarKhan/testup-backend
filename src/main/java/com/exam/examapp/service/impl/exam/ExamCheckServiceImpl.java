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
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.impl.user.UserServiceImpl;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.exam.ExamCheckService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.question.QuestionService;
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
    private static final String SUBJECT = "Sualiniz Yoxlandi";

    private static final String BODY = "Muellim sizin sualiniz yoxladi. Imtahan adi: %s";

    private final StudentExamRepository studentExamRepository;

    private final NotificationService notificationService;

    private final LogService logService;

    private final UserServiceImpl userService;

    private final ExamMapper examMapper;

    private final ExamService examService;

    private final QuestionService questionService;

    private static void getAnswerList(List<Question> questions, Map<UUID, AnswerStatus> questionIdToAnswerStatusMap, List<Double> list) {
        log.info("Cavab listi götürülür");
        for (Question question : questions) {
            if (QuestionType.TEXT_BASED.equals(question.getType()) || QuestionType.LISTENING.equals(question.getType())) {
                getAnswerList(question.getQuestions(), questionIdToAnswerStatusMap, list);
                continue;
            }
            AnswerStatus answerStatus = questionIdToAnswerStatusMap.get(question.getId());
            switch (answerStatus) {
                case AnswerStatus.HALF_CORRECT -> list.set(4, list.get(4) + 0.5);
                case AnswerStatus.QUARTER_CORRECT -> list.set(4, list.get(4) + 0.25);
                case AnswerStatus.THREE_QUARTERS_CORRECT -> list.set(4, list.get(4) + 0.75);
                case AnswerStatus.CORRECT -> {
                    switch (question.getType()) {
                        case QuestionType.SINGLE_CHOICE -> list.set(0, list.getFirst() + 1);
                        case QuestionType.MULTI_CHOICE -> list.set(1, list.get(1) + 1);
                        case QuestionType.MATCH -> list.set(2, list.get(2) + 1);
                        case QuestionType.OPEN_ENDED -> {
                            if (question.getQuestionDetails().isAuto()) list.set(3, list.get(3) + 1);
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
                            if (question.getQuestionDetails().isAuto()) list.set(8, list.get(8) + 1);
                            else list.set(9, list.get(9) + 1);
                        }
                    }
                }
            }
        }
    }

    private static String formatFormulaWithCounts(String formula, List<Double> correctAndWrongCounts) {
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
        log.info("Formula hazirlandi: {}", formattedFormula);
        return formattedFormula;
    }

    private StudentExam getStudentExam(UUID studentExamId) {
        return studentExamRepository.findById(studentExamId).orElseThrow(() -> new ResourceNotFoundException("Tələbə imtahanı tapılmadı"));
    }

    private List<ExamStatisticsStudent> getExamStudents(List<StudentExam> studentExams) {
        log.info("Tələbələr hazırlanır");
        List<ExamStatisticsStudent> list = studentExams.stream()
                .filter(studentExam -> ExamStatus.COMPLETED.equals(studentExam.getStatus()) ||
                        ExamStatus.EXPIRED.equals(studentExam.getStatus()) ||
                        ExamStatus.WAITING_OPEN_ENDED_QUESTION.equals(studentExam.getStatus()))
                .sorted(Comparator.comparing(StudentExam::getEndTime, Comparator.reverseOrder())
                        .reversed())
                .map(examStudent -> {
                    Instant endTime = examStudent.getEndTime();
                    long durationInSecond = endTime.getEpochSecond() - examStudent.getStartTime().getEpochSecond();
                    Integer examDurationLimit = examStudent.getExam().getDurationInSeconds();
                    if (examDurationLimit != null)
                        durationInSecond = examDurationLimit < durationInSecond ? examDurationLimit : durationInSecond;
                    return new ExamStatisticsStudent(examStudent.getExam().getId(), examStudent.getId(), examStudent.getStudent() == null ? examStudent.getStudentName() : examStudent.getStudent().getFullName(), durationInSecond, examStudent.getScore(), examStudent.getNumberOfQuestions(), examStudent.getNumberOfCorrectAnswers(), examStudent.getNumberOfNotCheckedYetQuestions() > 0);
                }).toList();
        log.info("Tələbələr hazırlandı");
        return list;
    }

    private ExamStatisticsRating getExamStatisticsRating(Exam exam) {
        log.info("İmtahan statistikası götürülür");
        Map<UUID, Integer> userIdToRatingMap = exam.getUserIdToRatingMap();
        Set<Map.Entry<UUID, Integer>> ratingEntries = userIdToRatingMap.entrySet();
        long totalFiveStarCount = ratingEntries.stream().filter(e -> e.getValue() == 5).count();
        long totalForStarCount = ratingEntries.stream().filter(e -> e.getValue() == 4).count();
        long totalThreeStarCount = ratingEntries.stream().filter(e -> e.getValue() == 3).count();
        long totalTwoStarCount = ratingEntries.stream().filter(e -> e.getValue() == 2).count();
        long totalOneStarCount = ratingEntries.stream().filter(e -> e.getValue() == 1).count();

        log.info("İmtahan statistikası götürüldü");
        return new ExamStatisticsRating(exam.getRating(), userIdToRatingMap.size(), totalFiveStarCount, totalForStarCount, totalThreeStarCount, totalTwoStarCount, totalOneStarCount);
    }

    private List<ExamStatisticsBestStudent> getBestStudents(List<StudentExam> studentExams) {
        log.info("Ən yaxşı tələbələr hazırlanır");
        List<ExamStatisticsBestStudent> list = studentExams.stream()
                .filter(studentExam ->
                        ExamStatus.COMPLETED.equals(studentExam.getStatus()) ||
                                ExamStatus.EXPIRED.equals(studentExam.getStatus()) ||
                                ExamStatus.WAITING_OPEN_ENDED_QUESTION.equals(studentExam.getStatus()))
                .sorted(Comparator.comparing(StudentExam::getScore, Comparator.reverseOrder()))
                .limit(5)
                .map(examStudent -> {
                    long durationInSecond = examStudent.getEndTime().getEpochSecond() - examStudent.getStartTime().getEpochSecond();
                    Integer examDurationLimit = examStudent.getExam().getDurationInSeconds();
                    if (examDurationLimit != null)
                        durationInSecond = examDurationLimit < durationInSecond ? examDurationLimit : durationInSecond;
                    return new ExamStatisticsBestStudent(examStudent.getExam().getId(), examStudent.getId(), examStudent.getStudent() == null ? examStudent.getStudentName() : examStudent.getStudent().getFullName(), durationInSecond, examStudent.getScore());
                }).toList();
        log.info("Ən yaxşı tələbələr hazırlandı");
        return list;
    }

    @Override
    @Transactional
    public ExamStatistics getExamStatistics(UUID id) {
        log.info("İmtahan statistikası hazırlanır: {}", id);
        List<StudentExam> studentExams = studentExamRepository.getByExam_Id(id);

        Exam exam = examService.getById(id);

        Integer participatedStudentCount = exam.getTeacher().getInfo().getExamToStudentCountMap().get(exam.getId());
        if (participatedStudentCount == null) participatedStudentCount = 0;
        ExamStatistics examStatistics = new ExamStatistics(getExamStatisticsRating(exam),
                getBestStudents(studentExams), getExamStudents(studentExams), participatedStudentCount, exam.getTeacher().getPack().getStudentPerExam());
        log.info("İmtahan statistikası: {}", examStatistics);
        return examStatistics;
    }

    @Override
    @Transactional
    public StartExamResponse getUserExam(UUID studentExamId) {
        log.info("İmtahan götürülür tələbə imtahan id-si: {}", studentExamId);
        StudentExam studentExam = getStudentExam(studentExamId);

        return new StartExamResponse(
                studentExamId,
                studentExam.getStatus(),
                studentExam.getQuestionIdToAnswerMap(),
                studentExam.getListeningIdToPlayTimeMap(),
                studentExam.getStartTime(),
                examMapper.toResponse(studentExam.getExam()));
    }

    @Override
    @Transactional
    public void checkAnswer(UUID studentExamId, UUID questionId, AnswerStatus status) {
        log.info("Sual yoxlanılır: {}, Sual id-si: {}, Status: {}", studentExamId, questionId, status);
        StudentExam studentExam = getStudentExam(studentExamId);

        Map<UUID, AnswerStatus> questionIdToAnswerStatusMap = studentExam.getQuestionIdToAnswerStatusMap();
        AnswerStatus answerStatus = questionIdToAnswerStatusMap.get(questionId);
        if (!AnswerStatus.WAITING_FOR_REVIEW.equals(answerStatus))
            throw new BadRequestException("Sual artıq yoxlanılmışdır");

        questionIdToAnswerStatusMap.remove(questionId);
        questionIdToAnswerStatusMap.put(questionId, answerStatus);

        List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions().stream().filter(subjectStructureQuestion -> !subjectStructureQuestion.getQuestion().stream().filter(question -> question.getId().equals(questionId)).toList().isEmpty()).toList();
        if (subjectStructureQuestions.isEmpty()) throw new BadRequestException("Subject structure question tapilmadi.");

        SubjectStructureQuestion subjectStructureQuestion = subjectStructureQuestions.getFirst();
        SubjectStructure subjectStructure = subjectStructureQuestion.getSubjectStructure();
        String formula = subjectStructure.getFormula();
        List<Question> questions = subjectStructureQuestion.getQuestion();
        int indexOf = questions.indexOf(questionService.getQuestionById(questionId)) + 1;

        String subjectName = subjectStructure.getSubject().getName();

        Map<Integer, AnswerStatus> integerAnswerStatusMap = studentExam.getSubjectToQuestionToAnswerStatus().get(subjectName);

        integerAnswerStatusMap.put(indexOf, status);

        int numberOfNotCheckedYetQuestions = studentExam.getNumberOfNotCheckedYetQuestions() - 1;
        studentExam.setNumberOfNotCheckedYetQuestions(numberOfNotCheckedYetQuestions);

        if (numberOfNotCheckedYetQuestions == 0) {
            studentExam.setStatus(ExamStatus.COMPLETED);
            List<UUID> uncheckedQuestionStudentExamIds = studentExam.getExam().getHasUncheckedQuestionStudentExamId();
            uncheckedQuestionStudentExamIds.remove(studentExamId);
            studentExam.getExam().setHasUncheckedQuestionStudentExamId(uncheckedQuestionStudentExamIds);
        }

        if (formula == null || formula.trim().isEmpty()) {
            double multiplayer = AnswerStatus.CORRECT.equals(status) ? 1.0 : AnswerStatus.HALF_CORRECT.equals(status) ? 0.5 : AnswerStatus.QUARTER_CORRECT.equals(status) ? 0.25 : AnswerStatus.THREE_QUARTERS_CORRECT.equals(status) ? 0.75 : 0.0;
            studentExam.setScore(studentExam.getScore() + subjectStructure.getQuestionToPointMap().get(indexOf) * multiplayer);
            if (AnswerStatus.CORRECT.equals(status) ||
                    AnswerStatus.HALF_CORRECT.equals(status) ||
                    AnswerStatus.QUARTER_CORRECT.equals(status) ||
                    AnswerStatus.THREE_QUARTERS_CORRECT.equals(status))
                studentExam.setNumberOfCorrectAnswers(studentExam.getNumberOfCorrectAnswers() + 1);
            else if (AnswerStatus.WRONG.equals(status))
                studentExam.setNumberOfWrongAnswers(studentExam.getNumberOfWrongAnswers() + 1);
            else if (AnswerStatus.NOT_ANSWERED.equals(status))
                studentExam.setNumberOfNotAnsweredQuestions(studentExam.getNumberOfNotAnsweredQuestions() + 1);
        } else studentExam.setScore(calculateScoreByFormula(studentExam));


        studentExamRepository.save(studentExam);
        log.info("Imtahan yoxlanıldı");
        sendNotification(studentExam);
        logService.save("Imtahan yoxlanıldı", userService.getCurrentUserOrNull());
    }

    private void sendNotification(StudentExam studentExam) {
        log.info("Bildiriş göndərilir");
        String examTitle = studentExam.getExam().getExamTitle();
        User student = studentExam.getStudent();
        if (student == null) {
            log.info("Telebe giris etmeden imtahan isleyib");
            log.info("Bildiriş göndərilmek olmadi");
            return;
        }
        String content = String.format(BODY, examTitle);
        notificationService.sendNotification(new NotificationRequest(SUBJECT, content, student.getEmail()));
        log.info("Bildiriş göndərildi");
    }

    private double calculateScoreByFormula(StudentExam studentExam) {
        log.info("İmtahan balı hesablanır");
        List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();
        Map<UUID, AnswerStatus> questionIdToAnswerStatusMap = studentExam.getQuestionIdToAnswerStatusMap();

        double score = 0;
        for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
            String formula = subjectStructureQuestion.getSubjectStructure().getFormula();
            List<Question> questions = subjectStructureQuestion.getQuestion();

            List<Double> list = new ArrayList<>(List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
            getAnswerList(questions, questionIdToAnswerStatusMap, list);
            String formatted = formatFormulaWithCounts(formula, list);
            score = new ExpressionBuilder(formatted).build().evaluate();
        }
        log.info("İmtahan balı hesablanılır : {}", score);
        return score;
    }
}
