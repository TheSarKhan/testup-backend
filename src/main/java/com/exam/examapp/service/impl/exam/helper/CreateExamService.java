package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.SubjectStructureQuestionsRequest;
import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.service.interfaces.TagService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateExamService {
    private final SubjectStructureService subjectStructureService;

    private final QuestionService questionService;

    private final TagService tagService;

    private final UserService userService;

    private final ExamRepository examRepository;

    private static Exam buildExam(ExamRequest request, List<SubjectStructureQuestion> subjectStructureQuestions, List<Tag> tags, BigDecimal cost, User user) {
        return Exam.builder()
                .examTitle(request.examTitle())
                .examDescription(request.examDescription())
                .subjectStructureQuestions(subjectStructureQuestions)
                .tags(tags)
                .durationInSeconds(request.durationInSeconds())
                .cost(cost)
                .isHidden(request.isHidden())
                .teacher(user)
                .isReadyForSale(request.isReadyForSale())
                .explanationVideoUrl(request.explanationVideoUrl())
                .build();
    }

    public UUID createExam(
            ExamRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {

        User user = userService.getCurrentUser();

        if (Role.TEACHER.equals(user.getRole())) ExamValidationService.validateRequest(request, user);

        log.info("Müəllim yoxlaması keçdi");

        List<SubjectStructureQuestion> subjectStructureQuestions =
                buildSubjectStructureQuestions(
                        request.subjectStructures(), titles, variantPictures, numberPictures, sounds);

        log.info("Mövzu strukturu sualları yaradıldı");

        List<Tag> tags = buildTags(request);

        BigDecimal cost = calculateCost(request, user);

        log.info("Xərc hesabladı");

        Exam exam = buildExam(request, subjectStructureQuestions, tags, cost, user);

        log.info("İmtahan quruldu");

        exam.setNumberOfQuestions(QuestionCountService.getQuestionCount(exam));

        log.info("Sualların sayı Hesablanır");
        Exam save = examRepository.save(exam);

        log.info("İmtahan saxlanıldı");
        UUID examId = save.getId();
        updateTeacherStatistics(user, examId);

        return examId;
    }

    private List<SubjectStructureQuestion> buildSubjectStructureQuestions(
            List<SubjectStructureQuestionsRequest> requests,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("Mövzu strukturu suallarının yaradılır");

        List<SubjectStructureQuestion> subjectStructureQuestions = new ArrayList<>();

        for (SubjectStructureQuestionsRequest req : requests) {
            SubjectStructure subjectStructure;
            if (req.subjectStructureRequest().submoduleId() == null) {
                subjectStructure = subjectStructureService.create(req.subjectStructureRequest());
            } else {
                subjectStructure = subjectStructureService.getBySubmoduleAndSubjectId(
                        req.subjectStructureRequest().submoduleId(),
                        req.subjectStructureRequest().subjectId()
                );
            }
            log.info("Mövzu strukturu yaradılmışdır");

            List<Question> questions = new ArrayList<>();
            List<QuestionRequest> questionRequests = req.questionRequests();
            log.info("Movzudaki sual sayi: {}", questionRequests.size());
            for (QuestionRequest questionRequest : questionRequests) {
                log.info("Sual yaradılır: {}", questionRequest.title());
                questions.add(questionService.save(questionRequest, titles, variantPictures, numberPictures, sounds));
                log.info("Sual yaradıldı. Sualın başlığı:{}", questionRequest.title());
            }

            log.info("Suallar yaradıldı.");
            subjectStructureQuestions.add(
                    SubjectStructureQuestion.builder()
                            .subjectStructure(subjectStructure)
                            .question(questions)
                            .build());
        }

        log.info("Mövzu strukturu suallarının yaradıldı");

        return subjectStructureQuestions;
    }

    private List<Tag> buildTags(ExamRequest request) {
        log.info("Etiketlər yaradılır");
        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.getTagById(request.headerTagId()));
        if (request.otherTagIds() != null) {
            tags.addAll(request.otherTagIds().stream().map(tagService::getTagById).toList());
        }
        log.info("Etiketlər yaradıldı");
        return tags;
    }

    private BigDecimal calculateCost(ExamRequest request, User user) {
        return Role.TEACHER.equals(user.getRole())
                ? null : request.cost() == null ? BigDecimal.ZERO : request.cost();
    }

    private void updateTeacherStatistics(User user, UUID examId) {
        if (Role.TEACHER.equals(user.getRole()) || Role.ADMIN.equals(user.getRole())) {
            user.getInfo().setCurrentlyTotalExamCount(user.getInfo().getCurrentlyTotalExamCount() + 1);
            user.getInfo().setThisMonthCreatedExamCount(user.getInfo().getThisMonthCreatedExamCount() + 1);
            TeacherInfo info = user.getInfo();
            Map<UUID, Integer> examToStudentCountMap = info.getExamToStudentCountMap();
            examToStudentCountMap.put(examId, 0);
            userService.save(user);
            log.info("İmtahan müəllim məlumatına əlavə edildi");
        }
    }
}
