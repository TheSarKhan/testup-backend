package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.SubjectStructureQuestionsRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ExamExpiredException;
import com.exam.examapp.exception.custom.ReachedLimitException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.ExamTeacher;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.repository.ExamTeacherRepository;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.repository.subject.SubjectStructureQuestionRepository;
import com.exam.examapp.service.impl.exam.checker.AnswerChecker;
import com.exam.examapp.service.impl.exam.checker.AnswerCheckerFactory;
import com.exam.examapp.service.interfaces.*;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;

    private final QuestionService questionService;

    private final SubjectStructureService subjectStructureService;

    private final TagService tagService;

    private final UserService userService;

    private final CacheService cacheService;

    private final StudentExamRepository studentExamRepository;

    private final SubjectStructureQuestionRepository subjectStructureQuestionRepository;

    private final ExamTeacherRepository examTeacherRepository;

    private final AnswerCheckerFactory factory;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final String EXAM_CODE_PREFIX = "exam_code_";

    private static String formatFormulaWithCounts(
            String formula, List<Integer> correctAndWrongCounts) {
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
        return formattedFormula;
    }

    private static Map<String, Map<Integer, AnswerStatus>> mapSubjectToQuestionAnswerStatus(StudentExam studentExam) {
        Map<String, Map<Integer, AnswerStatus>> subjectToQuestionToAnswerStatus = new HashMap<>();
        for (Map.Entry<UUID, AnswerStatus> uuidAnswerStatusEntry : studentExam.getQuestionIdToAnswerStatusMap().entrySet()) {
            UUID key = uuidAnswerStatusEntry.getKey();
            List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();
            for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
                String subject = subjectStructureQuestion.getSubjectStructure().getSubject().getName();
                List<Question> questions = subjectStructureQuestion.getQuestion();
                Map<Integer, AnswerStatus> questionToAnswerStatus = new HashMap<>();
                for (int i = 1; i <= questions.size(); i++) {
                    UUID questionId = questions.get(i - 1).getId();
                    if (questionId.equals(key)) {
                        questionToAnswerStatus.put(i, uuidAnswerStatusEntry.getValue());
                    }
                }
                subjectToQuestionToAnswerStatus.put(subject, questionToAnswerStatus);
            }
        }
        return subjectToQuestionToAnswerStatus;
    }

    private static Map<String, Map<Integer, String>> mapStudentAnswersToSubjects(StudentExam studentExam) {
        Map<String, Map<Integer, String>> subjectToQuestionToAnswer = new HashMap<>();
        for (Map.Entry<UUID, String> questionToAnswerEntry : studentExam.getQuestionIdToAnswerMap().entrySet()) {
            UUID key = questionToAnswerEntry.getKey();
            List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();
            for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
                String subject = subjectStructureQuestion.getSubjectStructure().getSubject().getName();
                List<Question> questions = subjectStructureQuestion.getQuestion();
                Map<Integer, String> questionToAnswer = new HashMap<>();
                for (int i = 1; i <= questions.size(); i++) {
                    UUID questionId = questions.get(i - 1).getId();
                    if (questionId.equals(key)) {
                        questionToAnswer.put(i, questionToAnswerEntry.getValue());
                    }
                }
                subjectToQuestionToAnswer.put(subject, questionToAnswer);
            }
        }
        return subjectToQuestionToAnswer;
    }

    @Override
    @Transactional
    public void createExam(
            ExamRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {

        User user = userService.getCurrentUser();

        if (Role.TEACHER.equals(user.getRole())) validateRequest(request, user);

        List<SubjectStructureQuestionsRequest> subjectStructureQuestionsRequests =
                request.subjectStructures();

        List<SubjectStructureQuestion> subjectStructureQuestions = new ArrayList<>();

        for (SubjectStructureQuestionsRequest subjectStructureQuestionsRequest :
                subjectStructureQuestionsRequests) {

            SubjectStructure subjectStructure =
                    subjectStructureService.create(
                            subjectStructureQuestionsRequest.subjectStructureRequest());

            List<Question> questions =
                    subjectStructureQuestionsRequest.questionRequests().stream()
                            .map(
                                    questionRequest ->
                                            questionService.save(
                                                    questionRequest, titles, variantPictures, numberPictures, sounds))
                            .toList();

            subjectStructureQuestions.add(
                    SubjectStructureQuestion.builder()
                            .subjectStructure(subjectStructure)
                            .question(questions)
                            .build());
        }

        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.getTagById(request.headerTagId()));
        if (request.otherTagIds() != null)
            tags.addAll(request.otherTagIds().stream().map(tagService::getTagById).toList());

        BigDecimal cost = Role.TEACHER.equals(user.getRole()) ? null : request.cost() == null ? BigDecimal.ZERO : request.cost();

        Exam exam =
                Exam.builder()
                        .examTitle(request.examTitle())
                        .examDescription(request.examDescription())
                        .subjectStructureQuestions(subjectStructureQuestions)
                        .tags(tags)
                        .durationInSeconds(request.durationInSeconds())
                        .cost(cost)
                        .isHidden(request.isHidden())
                        .teacher(user)
                        .explanationVideoUrl(request.explanationVideoUrl())
                        .build();

        exam.setNumberOfQuestions(getQuestionCount(exam));
        examRepository.save(exam);

        if (Role.TEACHER.equals(user.getRole())) {
            user.getInfo().setCurrentlyTotalExamCount(user.getInfo().getCurrentlyTotalExamCount() + 1);
            user.getInfo()
                    .setThisMonthCreatedExamCount(user.getInfo().getThisMonthCreatedExamCount() + 1);
            userService.save(user);
        }
    }

    private void validateRequest(ExamRequest request, User user) {
        if (Role.TEACHER.equals(user.getRole())
                && (user.getInfo().getThisMonthCreatedExamCount() >= user.getPack().getMonthlyExamCount()
                || user.getInfo().getCurrentlyTotalExamCount() >= user.getPack().getTotalExamCount()))
            throw new ReachedLimitException(
                    "You have reached the limit of exams for this month or total");

        Integer questionCountTotal =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsRequest::subjectStructureRequest)
                        .map(SubjectStructureRequest::questionCount)
                        .reduce(Integer::sum)
                        .orElse(0);

        if (questionCountTotal >= user.getPack().getQuestionCountPerExam())
            throw new ReachedLimitException("You have reached the limit of questions for this exam");

        List<QuestionRequest> questionRequests =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsRequest::questionRequests)
                        .reduce(
                                (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                        .orElseThrow(() -> new BadRequestException("Questions cannot be empty."));

        validate(
                user,
                questionRequests,
                request.hasPicture(),
                request.hasPdfPicture(),
                request.hasSound(),
                request.isHidden(),
                request.subjectStructures().size(),
                request.durationInSeconds());
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getMyExams() {
        User user = userService.getCurrentUser();
        if (Role.TEACHER.equals(user.getRole()) || Role.ADMIN.equals(user.getRole())) {
            return examRepository.getByTeacher(user).stream()
                    .map(ExamMapper::toBlockResponse)
                    .toList();
        } else {
            return studentExamRepository.getByStudent(user)
                    .stream()
                    .map(StudentExam::getExam)
                    .map(ExamMapper::toBlockResponse)
                    .toList();
        }
    }

    @Override
    public List<ExamBlockResponse> getAdminCooperationExams() {
        User user = userService.getCurrentUser();
        return examTeacherRepository.getByTeacher(user)
                .stream()
                .map(ExamTeacher::getExam)
                .map(ExamMapper::toBlockResponse)
                .toList();
    }

    @Override
    public List<ExamBlockResponse> getExamByTag(List<UUID> tagIds) {
        Specification<Exam> specification = Specification.unrestricted();
        for (UUID tagId : tagIds) {
            specification.or(ExamSpecification.hasTag(tagId));
        }
        return examRepository.findAll(specification)
                .stream()
                .map(ExamMapper::toBlockResponse)
                .toList();
    }

    @Override
    public List<ExamBlockResponse> getLastCreatedExams() {
        return examRepository.getLastCreated().stream().map(ExamMapper::toBlockResponse).toList();
    }

    @Override
    @Transactional
    public ExamResponse getExamById(UUID id) {
        return ExamMapper.toResponse(getById(id));
    }

    @Override
    public Integer getExamCode(UUID id) {
        Exam exam = getById(id);
        if (!exam.isHidden())
            throw new BadRequestException("Exam is not hidden. You cannot get the exam code.");
        int code = (int) (Math.random() * (9_999_999 - 1_000_000 + 1)) + 1_000_000;
        cacheService.saveContent(EXAM_CODE_PREFIX, String.valueOf(code), id.toString(), (long) 86_400_000);
        return code;
    }

    @Override
    @Transactional
    public StartExamResponse startExamViaCode(String studentName, String examCode) {
        String examId = cacheService.getContent(EXAM_CODE_PREFIX, examCode.substring(1));
        Exam byId = getById(UUID.fromString(examId));
        return startExam(studentName, byId.getId());
    }

    @Override
    @Transactional
    public StartExamResponse startExam(String studentName, UUID id) {
        User user = userService.getCurrentUserOrNull();
        Exam exam = getById(id);
        User examCreator = exam.getTeacher();

        if (user == null)
            return startExamWithoutLogin(studentName, id, examCreator, exam);

        List<StudentExam> byExamAndStudent = studentExamRepository.getByExamAndStudent(exam, user);

        if (byExamAndStudent.isEmpty()) {
            updateExamStudentCount(id, examCreator);
            return createStudentExamEntry(exam, user);
        } else if (byExamAndStudent.size() == 1
                && ExamStatus.ACTIVE.equals(byExamAndStudent.getFirst().getStatus())) {
            byExamAndStudent.getFirst().setStatus(ExamStatus.STARTED);
            byExamAndStudent.getFirst().setStartTime(Instant.now());
            return new StartExamResponse(
                    byExamAndStudent.getFirst().getId(),
                    ExamStatus.ACTIVE,
                    Map.of(),
                    Map.of(),
                    Instant.now(),
                    ExamMapper.toResponse(exam));
        } else if (byExamAndStudent.size() == 1
                && ExamStatus.STARTED.equals(byExamAndStudent.getFirst().getStatus())) {
            StudentExam first = byExamAndStudent.getFirst();

            if (first.getExam().getDurationInSeconds() == null) {
                return new StartExamResponse(
                        first.getId(),
                        ExamStatus.STARTED,
                        first.getQuestionIdToAnswerMap(),
                        first.getListeningIdToPlayTimeMap(),
                        first.getStartTime(),
                        ExamMapper.toResponse(exam));
            } else {
                if (Instant.now().plusSeconds(exam.getDurationInSeconds()).isBefore(first.getStartTime())) {
                    return new StartExamResponse(
                            first.getId(),
                            first.getStatus(),
                            first.getQuestionIdToAnswerMap(),
                            first.getListeningIdToPlayTimeMap(),
                            first.getStartTime(),
                            ExamMapper.toResponse(exam));
                } else {
                    first.setStatus(ExamStatus.EXPIRED);
                    calculateResult(first);
                    throw new ExamExpiredException("Exam has expired.");
                }
            }
        } else {
            return createStudentExamEntry(exam, user);
        }
    }

    private void calculateResult(StudentExam studentExam) {
        List<Integer> correctAndWrongCounts = new ArrayList<>(List.of(0,0,0,0,0,0,0,0));

        Map<UUID, AnswerStatus> answerStatusMap = checkAnswers(studentExam, correctAndWrongCounts);

        handleUncheckedQuestions(studentExam, answerStatusMap);

        calculateScore(studentExam, answerStatusMap, correctAndWrongCounts);

        updateStatistics(studentExam, answerStatusMap, correctAndWrongCounts);

        saveStudentExam(studentExam);
    }


    private Map<UUID, AnswerStatus> checkAnswers(StudentExam studentExam,
                                                 List<Integer> correctAndWrongCounts) {
        Map<UUID, String> questionIdToAnswerMap = studentExam.getQuestionIdToAnswerMap();
        List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();

        Map<UUID, AnswerStatus> answerStatusMap = new HashMap<>();

        for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
            for (Question question : subjectStructureQuestion.getQuestion()) {
                if (questionIdToAnswerMap.containsKey(question.getId())) {
                    AnswerChecker checker = factory.getChecker(question.getType());
                    checker.check(
                            question,
                            question.getQuestionDetails(),
                            questionIdToAnswerMap.get(question.getId()),
                            answerStatusMap,
                            correctAndWrongCounts
                    );
                } else {
                    answerStatusMap.put(question.getId(), AnswerStatus.NOT_ANSWERED);
                }
            }
        }

        return answerStatusMap;
    }


    private void handleUncheckedQuestions(StudentExam studentExam, Map<UUID, AnswerStatus> answerStatusMap) {
        long count = answerStatusMap.values().stream()
                .filter(AnswerStatus.WAITING_FOR_REVIEW::equals)
                .count();

        if (count > 0) {
            studentExam.getExam().getHasUncheckedQuestionStudentExamId().add(studentExam.getId());
            studentExam.setStatus(ExamStatus.WAITING_OPEN_ENDED_QUESTION);
            studentExam.setNumberOfNotCheckedYetQuestions((int) count);
            examRepository.save(studentExam.getExam());
        }
    }

    private void calculateScore(StudentExam studentExam,
                                Map<UUID, AnswerStatus> answerStatusMap,
                                List<Integer> correctAndWrongCounts) {
        for (SubjectStructureQuestion subjectStructureQuestion : studentExam.getExam().getSubjectStructureQuestions()) {
            String formula = subjectStructureQuestion.getSubjectStructure().getFormula();

            if (formula != null) {
                String formattedFormula = formatFormulaWithCounts(formula, correctAndWrongCounts);
                double score = new ExpressionBuilder(formattedFormula).build().evaluate();
                studentExam.setScore(score);
            } else {
                studentExam.setScore(calculatePointBasedScore(subjectStructureQuestion, answerStatusMap));
            }
        }
    }


    private double calculatePointBasedScore(SubjectStructureQuestion subjectStructureQuestion,
                                            Map<UUID, AnswerStatus> answerStatusMap) {
        Map<Integer, Integer> questionToPointMap = subjectStructureQuestion.getSubjectStructure().getQuestionToPointMap();
        List<Question> questions = subjectStructureQuestion.getQuestion();

        double score = 0;
        for (Map.Entry<Integer, Integer> entry : questionToPointMap.entrySet()) {
            Question currentQuestion = questions.get(entry.getKey());
            if (AnswerStatus.CORRECT.equals(answerStatusMap.get(currentQuestion.getId()))) {
                score += entry.getValue();
            }
        }
        return score;
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

        studentExam.setSubjectToQuestionToAnswer(mapStudentAnswersToSubjects(studentExam));
        studentExam.setSubjectToQuestionToAnswerStatus(mapSubjectToQuestionAnswerStatus(studentExam));
    }


    private void saveStudentExam(StudentExam studentExam) {
        studentExamRepository.save(studentExam);
    }

    private StartExamResponse startExamWithoutLogin(String studentName, UUID id, User examCreator, Exam exam) {
        updateExamStudentCount(id, examCreator);

        studentExamRepository.getByExamAndStudentName(exam, studentName).orElseThrow(() ->
                new BadRequestException("This student name already exists in this exam."));

        StudentExam save =
                studentExamRepository.save(
                        StudentExam.builder()
                                .numberOfQuestions(getQuestionCount(exam))
                                .status(ExamStatus.STARTED)
                                .studentName(studentName)
                                .startTime(Instant.now())
                                .exam(exam)
                                .build());

        return new StartExamResponse(
                save.getId(),
                ExamStatus.ACTIVE,
                Map.of(),
                Map.of(),
                Instant.now(),
                ExamMapper.toResponse(exam));
    }

    private void updateExamStudentCount(UUID id, User examCreator) {
        Map<UUID, Integer> examToStudentCountMap = examCreator.getInfo().getExamToStudentCountMap();
        User currentUserOrNull = userService.getCurrentUserOrNull();
        if (examToStudentCountMap != null &&
                (!(Role.TEACHER.equals(examCreator.getRole()) ||
                        (currentUserOrNull != null && examCreator.getId().equals(currentUserOrNull.getId()))) &&
                        examToStudentCountMap.get(id) >= examCreator.getPack().getStudentPerExam()))
            throw new ReachedLimitException("You have reached the limit of students for this exam");

        examToStudentCountMap = examToStudentCountMap == null ? new HashMap<>() : examToStudentCountMap;
        examToStudentCountMap.put(id, examToStudentCountMap.getOrDefault(id, 0) + 1);

        userService.save(examCreator);
    }

    private StartExamResponse createStudentExamEntry(Exam exam, User user) {
        StudentExam save =
                studentExamRepository.save(
                        StudentExam.builder()
                                .numberOfQuestions(getQuestionCount(exam))
                                .status(ExamStatus.STARTED)
                                .startTime(Instant.now())
                                .exam(exam)
                                .student(user)
                                .build());
        return new StartExamResponse(
                save.getId(),
                ExamStatus.ACTIVE,
                Map.of(),
                Map.of(),
                Instant.now(),
                ExamMapper.toResponse(exam));
    }

    @Override
    public ResultStatisticResponse finishExam(UUID studentExamId) {
        calculateResult(findStudentExamById(studentExamId));

        StudentExam studentExam = findStudentExamById(studentExamId);

        studentExam.setStatus(studentExam.getNumberOfNotCheckedYetQuestions() > 0 ?
                ExamStatus.WAITING_OPEN_ENDED_QUESTION : ExamStatus.COMPLETED);

        studentExam.setEndTime(Instant.now());
        studentExamRepository.save(studentExam);

        return getResultStatisticResponse(studentExamId, studentExam);
    }

    @Override
    public ResultStatisticResponse getResultStatistic(UUID studentExamId) {
        StudentExam studentExam = findStudentExamById(studentExamId);

        return getResultStatisticResponse(studentExamId, studentExam);
    }

    private ResultStatisticResponse getResultStatisticResponse(UUID studentExamId, StudentExam studentExam) {
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

    private StudentExam findStudentExamById(UUID studentExamId) {
        return studentExamRepository.findById(studentExamId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Student exam with id %s not found.", studentExamId)));
    }

    @Override
    @Transactional
    public void updateExam(
            ExamUpdateRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        User user = userService.getCurrentUser();
        Exam exam = getById(request.id());
        if (!Role.ADMIN.equals(user.getRole()) && user.getId().equals(exam.getTeacher().getId()))
            throw new BadRequestException("You cannot update this exam.");

        if (Role.TEACHER.equals(user.getRole())) {
            user.getInfo().setCurrentlyTotalExamCount(user.getInfo().getCurrentlyTotalExamCount() - 1);
            user.getInfo()
                    .setThisMonthCreatedExamCount(user.getInfo().getThisMonthCreatedExamCount() - 1);
            userService.save(user);
        }
        deleteExam(request.id());

        createExam(request.request(), titles, variantPictures, numberPictures, sounds);
    }

    private void validate(
            User user,
            List<QuestionRequest> questionRequests,
            boolean hasPicture,
            boolean hasPdfPicture,
            boolean hasSound,
            boolean hidden,
            int size,
            Integer integer) {
        Optional<QuestionRequest> fistManualCheckQuestion =
                questionRequests.stream()
                        .filter(
                                questionRequest ->
                                        QuestionType.OPEN_ENDED.equals(questionRequest.questionType())
                                                && (questionRequest.questionDetails().isAuto() != null
                                                && !questionRequest.questionDetails().isAuto()))
                        .findFirst();

        if (!user.getPack().isCanAddPicture() && hasPicture)
            throw new BadRequestException("You cannot add picture for this exam.");

        if (!user.getPack().isCanAddPdfSound() && (hasPdfPicture || hasSound))
            throw new BadRequestException("You cannot add sound or pdf picture for this exam.");

        if (hidden && !user.getPack().isCanShareViaCode())
            throw new BadRequestException(
                    "You cannot share this exam via code (cannot create hidden exam).");

        if (!user.getPack().isCanAddMultipleSubjectInOneExam() && size > 1)
            throw new BadRequestException("You cannot add more than one subject in one exam.");

        if (!user.getPack().isCanAddManualCheckAutoQuestion() && fistManualCheckQuestion.isPresent())
            throw new BadRequestException("You cannot add manual check question in this exam.");

        if (integer != null && !user.getPack().isCanSelectExamDuration())
            throw new BadRequestException("You cannot select exam duration.");
    }

    @Override
    public void deleteExam(UUID id) {
        Exam exam = getById(id);
        List<SubjectStructureQuestion> subjectStructureQuestions = exam.getSubjectStructureQuestions();
        for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
            List<Question> questions = subjectStructureQuestion.getQuestion();
            for (Question question : questions) {
                questionService.delete(question.getId());
            }
            subjectStructureService.delete(subjectStructureQuestion.getSubjectStructure().getId());
            subjectStructureQuestionRepository.deleteById(subjectStructureQuestion.getId());
        }
        examRepository.deleteById(id);
    }

    private int getQuestionCount(Exam exam) {
        return exam.getSubjectStructureQuestions().stream()
                .map(
                        subjectStructureQuestion ->
                                subjectStructureQuestion.getSubjectStructure().getQuestionCount())
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Exam getById(UUID id) {
        return examRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found."));
    }
}
