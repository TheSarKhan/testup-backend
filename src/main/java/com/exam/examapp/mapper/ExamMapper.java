package com.exam.examapp.mapper;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.QuestionDetailsResponseWithoutAnswer;
import com.exam.examapp.dto.response.QuestionResponseWithoutAnswer;
import com.exam.examapp.dto.response.UserResponseForExam;
import com.exam.examapp.dto.response.exam.*;
import com.exam.examapp.dto.response.subject.SubjectStructureQuestionResponseWithoutAnswer;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ExamMapper {
    public static ExamBlockResponse toBlockResponse(Exam exam, ExamStatus examStatus, UUID studentExamId) {
        Result result = getResult(exam);
        List<UUID> studentExamIds = exam.getHasUncheckedQuestionStudentExamId();
        Boolean hasUnchecked = studentExamIds == null ? null : !studentExamIds.isEmpty();
        return new ExamBlockResponse(
                exam.getId(),
                studentExamId,
                exam.getExamTitle(),
                result.first(),
                result.otherTags(),
                exam.getDurationInSeconds(),
                exam.getCost(),
                exam.getRating(),
                exam.isHidden(),
                exam.getNumberOfQuestions(),
                hasUnchecked,
                exam.isReadyForSale(),
                examStatus,
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    public static ExamBlockResponse toBlockResponse(Exam exam) {
        Result result = getResult(exam);
        List<UUID> studentExamIds = exam.getHasUncheckedQuestionStudentExamId();
        Boolean hasUnchecked = studentExamIds == null ? null : !studentExamIds.isEmpty();
        return new ExamBlockResponse(
                exam.getId(),
                null,
                exam.getExamTitle(),
                result.first(),
                result.otherTags(),
                exam.getDurationInSeconds(),
                exam.getCost(),
                exam.getRating(),
                exam.isHidden(),
                exam.getNumberOfQuestions(),
                hasUnchecked,
                exam.isReadyForSale(),
                null,
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    public static void update(Exam oldExam, ExamUpdateRequest request) {
        oldExam.setExamTitle(request.examTitle());
        oldExam.setExamDescription(request.examDescription());
        oldExam.setDurationInSeconds(request.durationInSeconds());
        oldExam.setCost(request.cost());
        oldExam.setHidden(request.isHidden());
        oldExam.setReadyForSale(request.isReadyForSale());
        oldExam.setExplanationVideoUrl(request.explanationVideoUrl());
    }

    public static ExamDetailedResponse toDetailedResponse(Exam exam, ExamStatus examStatus) {
        Result result = getResult(exam);
        List<UUID> studentExamIds = exam.getHasUncheckedQuestionStudentExamId();
        Boolean hasUnchecked = studentExamIds == null ? null : !studentExamIds.isEmpty();
        List<Subject> subjects = exam.getSubjectStructureQuestions().stream()
                .map(subjectStructureQuestion ->
                        subjectStructureQuestion.getSubjectStructure().getSubject())
                .toList();
        return new ExamDetailedResponse(
                exam.getId(),
                exam.getExamTitle(),
                result.first(),
                result.otherTags(),
                subjects,
                exam.getDurationInSeconds(),
                exam.getCost(),
                exam.getRating(),
                exam.isHidden(),
                exam.getNumberOfQuestions(),
                hasUnchecked,
                exam.isReadyForSale(),
                examStatus,
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    public static ExamStartLinkResponse toStartLinkResponse(Exam exam, List<String> subjectNames) {
        return new ExamStartLinkResponse(
                exam.getId(),
                exam.getStartId(),
                exam.getExamTitle(),
                exam.getDurationInSeconds(),
                exam.getNumberOfQuestions(),
                subjectNames,
                exam.isReadyForSale(),
                exam.isHidden());
    }

    private static Result getResult(Exam exam) {
        List<Tag> tags1 = exam.getTags();
        Tag first = tags1.getFirst();
        List<Tag> tags = new ArrayList<>(tags1);
        tags.remove(first);
        return new Result(first, tags);
    }

    @Transactional
    public ExamResponse toResponse(Exam exam) {
        Result result = getResult(exam);
        User user = exam.getTeacher();
        UserResponseForExam teacher =
                new UserResponseForExam(
                        user.getId(), user.getEmail(), user.getProfilePictureUrl(), user.getRole());
        return new ExamResponse(
                exam.getId(),
                exam.getExamTitle(),
                result.first,
                exam.getTags(),
                exam.getDurationInSeconds(),
                exam.getCost(),
                exam.getRating(),
                teacher,
                exam.getSubjectStructureQuestions(),
                exam.getExamDescription(),
                !(exam.getHasUncheckedQuestionStudentExamId() == null ||
                        exam.getHasUncheckedQuestionStudentExamId().isEmpty()),
                exam.getExplanationVideoUrl(),
                exam.getNumberOfQuestions(),
                exam.isReadyForSale(),
                exam.isHidden(),
                exam.isDeleted(),
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    @Transactional
    public ExamResponseWithoutAnswer toResponseWithoutAnswer(Exam exam) {
        Result result = getResult(exam);
        User user = exam.getTeacher();
        UserResponseForExam teacher =
                new UserResponseForExam(
                        user.getId(), user.getEmail(), user.getProfilePictureUrl(), user.getRole());
        List<SubjectStructureQuestion> subjectStructureQuestions = exam.getSubjectStructureQuestions();
        var withoutAnswers = subjectStructureQuestions.stream()
                .map(this::subjectStructureRemoveAnswer).toList();
        return new ExamResponseWithoutAnswer(
                exam.getId(),
                exam.getExamTitle(),
                result.first,
                exam.getTags(),
                exam.getDurationInSeconds(),
                exam.getCost(),
                exam.getRating(),
                teacher,
                withoutAnswers,
                exam.getExamDescription(),
                !(exam.getHasUncheckedQuestionStudentExamId() == null ||
                        exam.getHasUncheckedQuestionStudentExamId().isEmpty()),
                exam.getExplanationVideoUrl(),
                exam.getNumberOfQuestions(),
                exam.isReadyForSale(),
                exam.isHidden(),
                exam.isDeleted(),
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    private SubjectStructureQuestionResponseWithoutAnswer subjectStructureRemoveAnswer(SubjectStructureQuestion subjectStructureQuestion) {
        return new SubjectStructureQuestionResponseWithoutAnswer(
                subjectStructureQuestion.getId(),
                subjectStructureQuestion.getSubjectStructure(),
                subjectStructureQuestion.getQuestion().stream().map(this::questionRemoveAnswer).toList(),
                subjectStructureQuestion.getCreatedAt(),
                subjectStructureQuestion.getUpdatedAt()
        );
    }

    private QuestionResponseWithoutAnswer questionRemoveAnswer(Question question) {
        return new QuestionResponseWithoutAnswer(
                question.getId(),
                question.getTitle(),
                question.getTitleDescription(),
                question.isTitlePicture(),
                question.isTitleContainMath(),
                question.getType(),
                question.getDifficulty(),
                question.getTopic(),
                question.getSoundUrl(),
                question.getQuestionCount(),
                question.getQuestions().stream().map(this::questionRemoveAnswer).toList(),
                detailsRemoveAnswer(question.getQuestionDetails()),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    private QuestionDetailsResponseWithoutAnswer detailsRemoveAnswer(QuestionDetails details) {
        return new QuestionDetailsResponseWithoutAnswer(
                details.variantToContentMap(),
                details.variantToIsPictureMap(),
                details.variantToHasMathContentMap(),
                details.numberToContentMap(),
                details.numberToIsPictureMap(),
                details.numberToHasMathContentMap(),
                details.isAuto(),
                details.listeningTime()
        );
    }

    private record Result(Tag first, List<Tag> otherTags) {
    }
}
