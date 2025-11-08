package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.request.QuestionUpdateRequestForExam;
import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.*;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.exception.custom.UserNotLoginException;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.mapper.QuestionMapper;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.repository.ExamTeacherRepository;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.service.impl.exam.helper.*;
import com.exam.examapp.service.interfaces.CacheService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.TagService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private static final String EXAM_CODE_PREFIX = "exam_code_";

    private static final String EXAM_START_LINK_PREFIX = "/exam/start-info?id=";

    private final ExamRepository examRepository;

    private final QuestionService questionService;

    private final SubjectStructureService subjectStructureService;

    private final UserService userService;

    private final CacheService cacheService;

    private final StudentExamRepository studentExamRepository;

    private final ExamTeacherRepository examTeacherRepository;

    private final ExamResultService examResultService;

    private final StartExamService startExamService;

    private final CreateExamService createExamService;

    private final TagService tagService;

    private final LogService logService;

    @Value("${app.front-base-url}")
    private String frontBaseUrl;

    private static void printStartExam(Exam exam) {
        log.info("İmtahan başladı. İmtahan: {}", exam.getExamTitle());
    }

    @Override
    @Transactional
    public void createExam(
            ExamRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("İmtahan yaradılır");
        createExamService.createExam(request, titles, variantPictures, numberPictures, sounds);
        log.info("İmtahanın yaradılması tamamlandı");
        logService.save("İmtahanın yaradılması tamamlandı", userService.getCurrentUserOrNull());
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getAllExams(String name,
                                               Integer minCost,
                                               Integer maxCost,
                                               List<Integer> rating,
                                               List<UUID> tagIds,
                                               ExamSort sort,
                                               ExamType type,
                                               Integer pageNum) {
        Page<Exam> page = getExamPage(null, name, minCost, maxCost, rating, tagIds, pageNum, sort, type);

        return page.getContent()
                .stream()
                .filter(Exam::isDeleted)
                .filter(exam -> Role.ADMIN.equals(exam.getTeacher().getRole()))
                .filter(Exam::isReadyForSale)
                .map(examToResponse(userService.getCurrentUserOrNull()))
                .toList();
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getAllExamsForAdmin(String name,
                                                       Integer minCost,
                                                       Integer maxCost,
                                                       List<Integer> rating,
                                                       List<UUID> tagIds,
                                                       ExamSort sort,
                                                       ExamType type,
                                                       Integer pageNum) {
        Page<Exam> page = getExamPage(null, name, minCost, maxCost, rating, tagIds, pageNum, sort, type);

        return page.getContent()
                .stream()
                .filter(exam -> !exam.isDeleted())
                .map(examToResponse(userService.getCurrentUserOrNull()))
                .toList();
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getMyExams() {
        User user = userService.getCurrentUser();
        return getExamBlockResponses(user);
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getExamsByUserId(UUID id) {
        User user = userService.getUserById(id);
        return getExamBlockResponses(user);
    }

    private List<ExamBlockResponse> getExamBlockResponses(User user) {
        if (Role.TEACHER.equals(user.getRole()) || Role.ADMIN.equals(user.getRole())) {
            return examRepository.getByTeacher(user).stream()
                    .filter(exam -> !exam.isDeleted())
                    .map(exam -> ExamMapper.toBlockResponse(exam, null))
                    .toList();
        } else {
            return studentExamRepository.getByStudent(user)
                    .stream()
                    .filter(studentExam -> !studentExam.getExam().isDeleted())
                    .map(studentExam -> {
                        ExamStatus status = studentExam.getStatus();
                        return ExamMapper.toBlockResponse(studentExam.getExam(), status);
                    })
                    .toList();
        }
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getAdminCooperationExams() {
        User user = userService.getCurrentUser();
        return examTeacherRepository.getByTeacher(user)
                .stream()
                .filter(examTeacher -> !examTeacher.getExam().isDeleted())
                .map(examTeacher -> ExamMapper.toBlockResponse(
                        examTeacher.getExam(), null))
                .toList();
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getExamByTag(List<UUID> tagIds) {
        Specification<Exam> specification = Specification.unrestricted();
        for (UUID tagId : tagIds) {
            specification.or(ExamSpecification.hasTag(tagId));
        }
        User user = userService.getCurrentUserOrNull();
        return examRepository.findAll(specification)
                .stream()
                .filter(exam -> !exam.isDeleted())
                .filter(Exam::isReadyForSale)
                .map(examToResponse(user))
                .toList();
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getLastCreatedExams() {
        User user = userService.getCurrentUserOrNull();
        return examRepository.getLastCreated()
                .stream()
                .filter(exam -> !exam.isDeleted())
                .filter(Exam::isReadyForSale)
                .map(examToResponse(user))
                .toList();
    }

    @Override
    @Transactional
    public ExamDetailedResponse getExamDetailedById(UUID id) {
        Exam exam = getById(id);
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");

        User user = userService.getCurrentUserOrNull();
        log.info("point 1");
        if (user != null) {
            List<StudentExam> studentExams = studentExamRepository.getByStudent(user);
            log.info("point 2");
            List<StudentExam> filteredExams = studentExams
                    .stream()
                    .filter(studentExam -> studentExam.getExam().equals(exam))
                    .toList();
            log.info("point 3");
            StudentExam last = filteredExams.getLast();
            log.info("point 4");
            return ExamMapper.toDetailedResponse(exam, last == null ? null : last.getStatus());
        }
        return ExamMapper.toDetailedResponse(exam, null);
    }

    @Override
    @Transactional
    public ExamStartLinkResponse getExamStartInformationById(UUID id) {
        Exam exam = examRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("İmtahan tapılmadı"));
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");

        List<String> subjectNames = exam.getSubjectStructureQuestions()
                .stream()
                .map(subjectStructureQuestion ->
                        subjectStructureQuestion.getSubjectStructure().getSubject().getName())
                .toList();
        return ExamMapper.toStartLinkResponse(exam, subjectNames);
    }

    @Override
    @Transactional
    public ExamResponse getExamById(UUID id) {
        return ExamMapper.toResponse(getById(id));
    }

    @Override
    @Transactional
    public Integer getExamCode(UUID id) {
        Exam exam = getById(id);
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");
        if (!exam.isHidden())
            throw new BadRequestException("İmtahan gizli deyil. İmtahan kodunu əldə edə bilməzsiniz");
        int code = (int) (Math.random() * (9_999_999 - 1_000_000 + 1)) + 1_000_000;
        cacheService.saveContent(EXAM_CODE_PREFIX, String.valueOf(code), id.toString(), (long) 86_400_000);
        return code;
    }

    @Override
    @Transactional
    public String getExamLink(UUID id) {
        Exam exam = getById(id);
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");
        return frontBaseUrl + EXAM_START_LINK_PREFIX + exam.getId();
    }

    @Override
    @Transactional
    public StartExamResponse startExamViaCode(String studentName, String examCode) {
        log.info("Kod vasitəsilə imtahan başlayır: {}", examCode);
        if (examCode == null || examCode.length() != 8)
            throw new BadRequestException("İmtahan Kodunun uzunluğu 8 olmalıdır. İmtahan Kodu: " + examCode);

        String examId = cacheService.getContent(EXAM_CODE_PREFIX, examCode.substring(1));
        Exam exam = getById(UUID.fromString(examId));
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");
        StartExamResponse result = startExamService.startExam(studentName, exam);
        printStartExam(exam);
        logService.save("İmtahan başladı. İmtahan: " + exam.getExamTitle(), userService.getCurrentUserOrNull());
        return getResponse(result);
    }

    @Override
    @Transactional
    public StartExamResponse startExamViaId(String studentName, UUID id) {
        log.info("Id vasitəsilə imtahan başlayır : {}", id);
        Exam exam = examRepository.getExamByStartId(id).orElseThrow(() ->
                new ResourceNotFoundException("İmtahan tapılmadı"));
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");
        StartExamResponse result = startExamService.startExam(studentName, exam);
        printStartExam(exam);
        logService.save("İmtahan başladı. İmtahan: " + exam.getExamTitle(), userService.getCurrentUserOrNull());
        return result;
    }

    @Override
    @Transactional
    public ResultStatisticResponse finishExam(UUID studentExamId) {
        log.info("İmtahan id ilə bitirilir: {}", studentExamId);
        examResultService.calculateResult(findStudentExamById(studentExamId));

        StudentExam studentExam = findStudentExamById(studentExamId);

        studentExam.setStatus(studentExam.getNumberOfNotCheckedYetQuestions() > 0 ?
                ExamStatus.WAITING_OPEN_ENDED_QUESTION : ExamStatus.COMPLETED);

        studentExam.setEndTime(Instant.now());
        studentExamRepository.save(studentExam);

        log.info("İmtahan bitirildi");
        return examResultService.getResultStatisticResponse(studentExamId, studentExam);
    }

    @Override
    @Transactional
    public ResultStatisticResponse getResultStatistic(UUID studentExamId) {
        log.info("İmtahan üçün id ilə nəticə statistikasının alınması: {}", studentExamId);
        StudentExam studentExam = findStudentExamById(studentExamId);

        return examResultService.getResultStatisticResponse(studentExamId, studentExam);
    }

    @Override
    @Transactional
    public void publishExam(UUID id) {
        log.info("İmtahan id ilə nəşr olunur. Id: {}", id);
        User user = userService.getCurrentUser();
        Exam exam = getById(id);
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");
        if (!Role.ADMIN.equals(user.getRole()) || exam.getTeacher().getId() != user.getId())
            throw new BadRequestException("Əgər admin deyilsinizsə, başqasının imtahanını dərc edə bilməzsiniz.");
        exam.setReadyForSale(true);
        examRepository.save(exam);
        log.info("İmtahan id ilə nəşr olundu. Id: {}", id);
    }

    @Override
    @Transactional
    public void unpublishExam(UUID id) {
        log.info("İmtahan id ilə nəşrdən toplanır. Id: {}", id);
        User user = userService.getCurrentUser();
        Exam exam = getById(id);
        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");
        if (!Role.ADMIN.equals(user.getRole()) || exam.getTeacher().getId() != user.getId())
            throw new BadRequestException("Əgər admin deyilsinizsə, başqasının imtahanını dərcinə mane ola bilməzsiniz.");
        exam.setReadyForSale(false);
        examRepository.save(exam);
        log.info("İmtahan id ilə nəşrdən toplandı. Id: {}", id);
    }

    @Override
    @Transactional
    public void updateExam(
            ExamUpdateRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("İmtahan id ilə yenilənir: {}", request.id());
        Exam exam = getById(request.id());

        if (exam.isDeleted())
            throw new BadRequestException("Imtahan silinib.");

        ExamValidationService.validationForUpdate(request, exam.getTeacher());

        log.info("Imtahan validasiyadan keçdi");
        ExamMapper.update(exam, request);

        log.info("Imtahan update üçün xəritələndi edildi");
        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.getTagById(request.headerTagId()));
        if (request.otherTagIds() != null) {
            tags.addAll(request.otherTagIds().stream().map(tagService::getTagById).toList());
        }

        exam.setTags(tags);

        log.info("Etiketlər hazırlanmışdır");

        var subjectStructureQuestionsUpdateRequests = request.subjectStructures();
        List<SubjectStructureQuestion> subjectStructureQuestions = new ArrayList<>();
        for (var subjectStructureQuestionsUpdateRequest : subjectStructureQuestionsUpdateRequests) {
            SubjectStructureQuestion subjectStructureQuestion = new SubjectStructureQuestion();
            var subjectStructureUpdateRequest = subjectStructureQuestionsUpdateRequest
                    .subjectStructureUpdateRequest();
            SubjectStructure subjectStructure;
            if (subjectStructureUpdateRequest.request().submoduleId() != null) {
                subjectStructure = subjectStructureService
                        .getById(subjectStructureUpdateRequest.id());
            } else {
                subjectStructureService.delete(subjectStructureUpdateRequest.id());
                subjectStructure = subjectStructureService
                        .create(subjectStructureUpdateRequest.request());
            }
            subjectStructureQuestion.setSubjectStructure(subjectStructure);

            var questionUpdateRequestForExams = subjectStructureQuestionsUpdateRequest.questionRequests();
            List<Question> questions = new ArrayList<>();
            for (QuestionUpdateRequestForExam questionUpdateRequestForExam : questionUpdateRequestForExams) {
                Question question;
                if (questionUpdateRequestForExam.hasChange()) {
                    questionService.delete(questionUpdateRequestForExam.id());
                    question = questionService.save(
                            QuestionMapper.requestToRequest(questionUpdateRequestForExam),
                            titles, variantPictures, numberPictures, sounds);

                } else {
                    question = questionService.getQuestionById(questionUpdateRequestForExam.id());
                }
                questions.add(question);
            }
            subjectStructureQuestion.setQuestion(questions);
            subjectStructureQuestions.add(subjectStructureQuestion);
        }
        exam.setSubjectStructureQuestions(subjectStructureQuestions);
        examRepository.save(exam);
        log.info("İmtahan yeniləndi. Id: {}", exam.getId());
        logService.save("İmtahan yeniləndi. Id: " + exam.getId(), userService.getCurrentUserOrNull());
    }

    @Override
    @Transactional
    public void deleteExam(UUID id) {
        log.info("İmtahan id ilə silinir: {}", id);
        User user = userService.getCurrentUser();
        Exam exam = getById(id);
        if (!Role.ADMIN.equals(user.getRole()) && exam.getTeacher().getId() != user.getId())
            throw new BadRequestException("Əgər admin deyilsinizsə, başqasının imtahanını silə bilməzsiniz.");
        exam.setDeleted(true);
        examRepository.save(exam);
        log.info("İmtahan silindi");
        logService.save("İmtahan silindi", userService.getCurrentUserOrNull());
    }

    @Override
    @Transactional
    public Exam getById(UUID id) {
        log.info("İd ilə imtahan verilir: {}", id);
        return examRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("İmtahan tapılmadı"));
    }

    private StudentExam findStudentExamById(UUID studentExamId) {
        return studentExamRepository.findById(studentExamId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("%s id ilə tələbə imtahanı tapılmadı.", studentExamId)));
    }

    @Override
    @Transactional
    public Function<Exam, ExamBlockResponse> examToResponse(User user) {
        return exam -> {
            if (user != null && !(Role.ADMIN.equals(user.getRole()) || Role.TEACHER.equals(user.getRole()))) {
                List<StudentExam> studentExams = studentExamRepository.getByStudent(user);
                List<StudentExam> filteredExams = studentExams
                        .stream()
                        .filter(studentExam -> studentExam.getExam().equals(exam))
                        .toList();
                StudentExam last = filteredExams.getLast();
                return ExamMapper.toBlockResponse(exam, last == null ? null : last.getStatus());
            }
            return ExamMapper.toBlockResponse(exam, null);
        };
    }

    @Override
    @Transactional
    public Page<Exam> getExamPage(UUID teacherId,
                                  String name,
                                  Integer minCost,
                                  Integer maxCost,
                                  List<Integer> rating,
                                  List<UUID> tagIds,
                                  Integer pageNum,
                                  ExamSort sort,
                                  ExamType type) {
        Specification<Exam> specification = Specification.unrestricted();
        specification = specification.and(ExamSpecification.hasName(name))
                .and(ExamSpecification.hasCostBetween(minCost, maxCost))
                .and(ExamSpecification.hasRatingInRange(rating))
                .and(ExamSpecification.hasTags(tagIds))
                .and(ExamSpecification.hasTeacher(teacherId));

        User currentUser = userService.getCurrentUserOrNull();
        if ((type == ExamType.BOUGHT || type == ExamType.FINISHED) && currentUser == null) {
            throw new UserNotLoginException("User must be logged in to see bought or finished exams");
        }

        specification = specification.and(
                ExamSpecification.hasType(type, currentUser, studentExamRepository)
        );

        Sort sortBy = switch (sort) {
            case COST_ASC -> Sort.by("cost").ascending();
            case COST_DESC -> Sort.by("cost").descending();
            case RATING_ASC -> Sort.by("rating").ascending();
            case RATING_DESC -> Sort.by("rating").descending();
            case QUESTION_COUNT_ASC -> Sort.by("numberOfQuestions").ascending();
            case QUESTION_COUNT_DESC -> Sort.by("numberOfQuestions").descending();
            case CREATED_DATE_ASC -> Sort.by("createdAt").ascending();
            case CREATED_DATE_DESC -> Sort.by("createdAt").descending();
            default -> Sort.unsorted();
        };

        pageNum = (pageNum != null && pageNum > 0) ? pageNum - 1 : 0;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNum, pageSize, sortBy);

        return examRepository.findAll(specification, pageable);
    }

    @Transactional
    public StartExamResponse getResponse(StartExamResponse response) {
        return new StartExamResponse(
                response.studentExamId(),
                response.status(),
                response.questionIdToAnswerMap(),
                response.listeningIdToPlayTimeMap(),
                response.startTime(),
                ExamMapper.toResponse(getById(response.exam().id())));
    }
}
