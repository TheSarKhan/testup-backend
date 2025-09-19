package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.service.interfaces.exam.StudentExamService;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student-exam")
@Tag(
    name = "Student Exam",
    description =
        "Operations related to student exams: assigning exams, playing listening, saving answers")
public class StudentExamController {
  private final StudentExamService studentExamService;

  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Assign Exam to Student",
      description = "ADMIN assigns an exam to a student by their IDs")
  public ResponseEntity<ApiResponse<Void>> addExam(
      @RequestParam UUID studentId, @RequestParam UUID examId) {
    studentExamService.addExam(studentId, examId);
    return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Exam added successfully", null));
  }

  @PatchMapping("/listening")
  @SecurityRequirement(name = "bearerAuth")
  @Operation(
      summary = "Play Listening",
      description = "Mark a listening section as played for a student's exam")
  public ResponseEntity<ApiResponse<Void>> playListening(
      @RequestParam UUID studentExamId, @RequestParam UUID listeningId) {
    studentExamService.listeningPlayed(studentExamId, listeningId);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Listening played successfully", null));
  }

  @PatchMapping(value = "/answer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @SecurityRequirement(name = "bearerAuth")
  @Operation(
      summary = "Save Answer",
      description = "Save a student's answer to a question in an exam")
  public ResponseEntity<ApiResponse<Void>> saveAnswer(
      @RequestPart UUID studentExamId,
      @RequestPart UUID questionId,
      @RequestPart(required = false) String answer,
      @RequestPart(required = false) MultipartFile file) {
    studentExamService.saveAnswer(studentExamId, questionId, answer, file);
    return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Answer saved successfully", null));
  }
}
