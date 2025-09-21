package com.exam.examapp.mapper;

import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.dto.response.UserResponseForExam;
import com.exam.examapp.model.Tag;
import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExamMapper {
    public static ExamBlockResponse toBlockResponse(Exam exam) {
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
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    private static Result getResult(Exam exam) {
        List<Tag> tags1 = exam.getTags();
        Tag first = tags1.getFirst();
        List<Tag> tags = new ArrayList<>(tags1);
        tags.remove(first);
        return new Result(first, tags);
    }

    public static ExamResponse toResponse(Exam exam) {
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
                !exam.getHasUncheckedQuestionStudentExamId().isEmpty(),
                exam.getExplanationVideoUrl(),
                exam.getNumberOfQuestions(),
                exam.isHidden(),
                exam.isDeleted(),
                exam.getCreatedAt(),
                exam.getUpdatedAt());
    }

    private record Result(Tag first, List<Tag> otherTags) {
    }
}
