package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.UserResponseForExam;
import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.dto.response.exam.ExamDetailedResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.dto.response.exam.ExamStartLinkResponse;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.subject.Subject;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ExamMapper {
    public static ExamBlockResponse toBlockResponse(Exam exam, ExamStatus examStatus) {
        Result result = getResult(exam);
        List<UUID> studentExamIds = exam.getHasUncheckedQuestionStudentExamId();
        Boolean hasUnchecked = studentExamIds == null ? null : !studentExamIds.isEmpty();
        return new ExamBlockResponse(
                exam.getId(),
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

    private record Result(Tag first, List<Tag> otherTags) {
    }
}
