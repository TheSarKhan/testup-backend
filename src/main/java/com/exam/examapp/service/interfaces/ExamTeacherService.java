package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.AddExamTeacherRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExamTeacherService {
  String addExamTeacher(AddExamTeacherRequest request);

  void removeExamTeacher(UUID examId, UUID teacherId);
}
