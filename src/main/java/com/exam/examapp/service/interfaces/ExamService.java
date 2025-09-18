package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.ExamRequest;
import com.exam.examapp.dto.request.ExamUpdateRequest;
import com.exam.examapp.dto.response.ExamBlockResponse;
import com.exam.examapp.dto.response.ExamResponse;
import com.exam.examapp.dto.response.StartExamResponse;
import java.util.List;
import java.util.UUID;

import com.exam.examapp.model.exam.Exam;
import org.springframework.web.multipart.MultipartFile;

public interface ExamService {
  void createExam(
      ExamRequest request,
      List<MultipartFile> titles,
      List<MultipartFile> variantPictures,
      List<MultipartFile> numberPictures,
      List<MultipartFile> sounds);

  List<ExamBlockResponse> getMyExams();

  List<ExamBlockResponse> getAdminCooperationExams();

  List<ExamBlockResponse> getExamByTag(List<UUID> tagIds);

  List<ExamBlockResponse> getLastCreatedExams();

  ExamResponse getExamById(UUID id);

  Exam getById(UUID id);

  Integer getExamCode(UUID id);

  StartExamResponse startExamViaCode(String studentName, String examCode);

  StartExamResponse startExam(String studentName, UUID id);

  void finishExam(UUID examId);

  void updateExam(
      ExamUpdateRequest request,
      List<MultipartFile> titles,
      List<MultipartFile> variantPictures,
      List<MultipartFile> numberPictures,
      List<MultipartFile> sounds);

  void deleteExam(UUID id);
}
