package com.exam.examapp.service.interfaces.exam;

import com.exam.examapp.dto.request.exam.AddExamTeacherRequest;

import java.util.UUID;

public interface ExamTeacherService {
  String addExamTeacher(AddExamTeacherRequest request);

  void removeExamTeacher(UUID examId, UUID teacherId);
}
