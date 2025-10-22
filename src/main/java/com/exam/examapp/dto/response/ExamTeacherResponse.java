package com.exam.examapp.dto.response;

import com.exam.examapp.model.subject.Subject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ExamTeacherResponse(
        UUID examId,
        List<TeacherResponse> teacherResponses,
        Map<Subject, Integer> subjectToQuestionCountMap
) {
}
