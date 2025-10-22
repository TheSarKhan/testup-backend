package com.exam.examapp.service.interfaces.exam;

import com.exam.examapp.dto.request.exam.AddExamTeacherRequest;
import com.exam.examapp.dto.response.ExamTeacherResponse;

import java.util.UUID;

public interface ExamTeacherService {
    String addExamTeacher(AddExamTeacherRequest request);

    ExamTeacherResponse getExamTeacher(UUID examId);

    void removeExamTeacher(UUID examId, UUID teacherId);
}
