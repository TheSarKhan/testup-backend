package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.request.SubjectStructureQuestionsRequest;
import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.model.Tag;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public void createExam(
            ExamRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {

        User user = userService.getCurrentUser();

        if (Role.TEACHER.equals(user.getRole())) ExamValidationService.validateRequest(request, user);

        List<SubjectStructureQuestion> subjectStructureQuestions =
                buildSubjectStructureQuestions(
                        request.subjectStructures(), titles, variantPictures, numberPictures, sounds);

        List<Tag> tags = buildTags(request);

        BigDecimal cost = calculateCost(request, user);

        Exam exam = buildExam(request, subjectStructureQuestions, tags, cost, user);

        exam.setNumberOfQuestions(QuestionCountService.getQuestionCount(exam));
        examRepository.save(exam);

        updateTeacherStatistics(user);
    }

    private List<SubjectStructureQuestion> buildSubjectStructureQuestions(
            List<SubjectStructureQuestionsRequest> requests,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {

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

            List<Question> questions =
                    req.questionRequests().stream()
                            .map(q -> questionService.save(q, titles, variantPictures, numberPictures, sounds))
                            .toList();

            subjectStructureQuestions.add(
                    SubjectStructureQuestion.builder()
                            .subjectStructure(subjectStructure)
                            .question(questions)
                            .build());
        }

        return subjectStructureQuestions;
    }

    private List<Tag> buildTags(ExamRequest request) {
        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.getTagById(request.headerTagId()));
        if (request.otherTagIds() != null) {
            tags.addAll(request.otherTagIds().stream().map(tagService::getTagById).toList());
        }
        return tags;
    }

    private BigDecimal calculateCost(ExamRequest request, User user) {
        return Role.TEACHER.equals(user.getRole())
                ? null : request.cost() == null ? BigDecimal.ZERO : request.cost();
    }

    private void updateTeacherStatistics(User user) {
        if (Role.TEACHER.equals(user.getRole())) {
            user.getInfo().setCurrentlyTotalExamCount(user.getInfo().getCurrentlyTotalExamCount() + 1);
            user.getInfo().setThisMonthCreatedExamCount(user.getInfo().getThisMonthCreatedExamCount() + 1);
            userService.save(user);
        }
    }
}
