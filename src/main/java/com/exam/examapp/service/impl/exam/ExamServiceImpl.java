package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.dto.response.exam.ExamDetailedResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.repository.ExamTeacherRepository;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.repository.subject.SubjectStructureQuestionRepository;
import com.exam.examapp.service.impl.exam.helper.CreateExamService;
import com.exam.examapp.service.impl.exam.helper.ExamResultService;
import com.exam.examapp.service.impl.exam.helper.StartExamService;
import com.exam.examapp.service.interfaces.CacheService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private static final String EXAM_CODE_PREFIX = "exam_code_";
    private final ExamRepository examRepository;
    private final QuestionService questionService;
    private final SubjectStructureService subjectStructureService;
    private final UserService userService;
    private final CacheService cacheService;
    private final StudentExamRepository studentExamRepository;
    private final SubjectStructureQuestionRepository subjectStructureQuestionRepository;
    private final ExamTeacherRepository examTeacherRepository;
    private final ExamResultService examResultService;
    private final StartExamService startExamService;
    private final CreateExamService createExamService;

    @Override
    @Transactional
    public void createExam(
            ExamRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        createExamService.createExam(request, titles, variantPictures, numberPictures, sounds);
    }

    @Override
    @Transactional
    public List<ExamBlockResponse> getMyExams() {
        User user = userService.getCurrentUser();
        if (Role.TEACHER.equals(user.getRole()) || Role.ADMIN.equals(user.getRole())) {
            return examRepository.getByTeacher(user).stream()
                    .map(exam -> ExamMapper.toBlockResponse(exam, null))
                    .toList();
        } else {
            return studentExamRepository.getByStudent(user)
                    .stream()
                    .map(studentExam -> {
                        ExamStatus status = studentExam.getStatus();
                        return ExamMapper.toBlockResponse(studentExam.getExam(), status);
                    })
                    .toList();
        }
    }

    @Override
    public List<ExamBlockResponse> getAdminCooperationExams() {
        User user = userService.getCurrentUser();
        return examTeacherRepository.getByTeacher(user)
                .stream()
                .map(examTeacher -> ExamMapper.toBlockResponse(
                        examTeacher.getExam(), null))
                .toList();
    }

    @Override
    public List<ExamBlockResponse> getExamByTag(List<UUID> tagIds) {
        Specification<Exam> specification = Specification.unrestricted();
        for (UUID tagId : tagIds) {
            specification.or(ExamSpecification.hasTag(tagId));
        }
        User user = userService.getCurrentUserOrNull();
        return examRepository.findAll(specification)
                .stream()
                .map(examToResponse(user))
                .toList();
    }

    @Override
    public List<ExamBlockResponse> getLastCreatedExams() {
        User user = userService.getCurrentUserOrNull();
        return examRepository.getLastCreated()
                .stream()
                .map(examToResponse(user))
                .toList();
    }

    @Override
    public ExamDetailedResponse getExamDetailedById(UUID id) {
        Exam exam = getById(id);
        User user = userService.getCurrentUserOrNull();
        if (user != null) {
            List<StudentExam> studentExams = studentExamRepository.getByStudent(user);
            List<StudentExam> filteredExams = studentExams
                    .stream()
                    .filter(studentExam -> studentExam.getExam().equals(exam))
                    .toList();
            StudentExam last = filteredExams.getLast();
            return ExamMapper.toDetailedResponse(exam, last == null ? null : last.getStatus());
        }
        return ExamMapper.toDetailedResponse(exam, null);
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
        Exam exam = getById(UUID.fromString(examId));
        return startExamService.startExam(studentName, exam);
    }

    @Override
    @Transactional
    public StartExamResponse startExamViaId(String studentName, UUID id) {
        Exam exam = getById(id);
        return startExamService.startExam(studentName, exam);
    }

    @Override
    public ResultStatisticResponse finishExam(UUID studentExamId) {
        examResultService.calculateResult(findStudentExamById(studentExamId));

        StudentExam studentExam = findStudentExamById(studentExamId);

        studentExam.setStatus(studentExam.getNumberOfNotCheckedYetQuestions() > 0 ?
                ExamStatus.WAITING_OPEN_ENDED_QUESTION : ExamStatus.COMPLETED);

        studentExam.setEndTime(Instant.now());
        studentExamRepository.save(studentExam);

        return examResultService.getResultStatisticResponse(studentExamId, studentExam);
    }

    @Override
    public ResultStatisticResponse getResultStatistic(UUID studentExamId) {
        StudentExam studentExam = findStudentExamById(studentExamId);

        return examResultService.getResultStatisticResponse(studentExamId, studentExam);
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

    @Override
    public Exam getById(UUID id) {
        return examRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found."));
    }

    private StudentExam findStudentExamById(UUID studentExamId) {
        return studentExamRepository.findById(studentExamId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Student exam with id %s not found.", studentExamId)));
    }

    private Function<Exam, ExamBlockResponse> examToResponse(User user) {
        return exam -> {
            if (user != null) {
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
}
