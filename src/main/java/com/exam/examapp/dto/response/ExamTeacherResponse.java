package com.exam.examapp.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ExamTeacherResponse(
        UUID examId,
        List<TeacherResponse> teacherResponses,
        Map<String, Integer> subjectToQuestionCountMap
) {
}
