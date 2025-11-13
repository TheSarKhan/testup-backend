package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.dto.response.exam.statistic.ExamStatistics;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.service.interfaces.exam.ExamCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam-check")
@Tag(name = "Exam Check", description = "Exam checking and statistics APIs (Admin, Teacher)")
public class ExamCheckController {
    private final ExamCheckService examCheckService;

    @GetMapping("/statistics")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get exam statistics", description = "Only admins and teachers can access exam statistics")
    public ResponseEntity<ApiResponse<ExamStatistics>> statistics(@RequestParam UUID examId) {
        ExamStatistics examStatistics = examCheckService.getExamStatistics(examId);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahan statistikası", examStatistics));
    }

    @GetMapping("/student-answers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get student answers", description = "Admins and teachers can view student exam answers")
    public ResponseEntity<ApiResponse<StartExamResponse>> getUserExam(@RequestParam UUID studentExamId) {
        StartExamResponse startExamResponse = examCheckService.getUserExam(studentExamId);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahan uğurla başladı", startExamResponse));
    }

    @PatchMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Check student answer", description = "Admins and teachers can check and update a student's exam answer status")
    public ResponseEntity<ApiResponse<Void>> checkAnswer(@RequestParam UUID studentExamId,
                                                         @RequestParam UUID questionId,
                                                         @RequestParam AnswerStatus status) {
        examCheckService.checkAnswer(studentExamId, questionId, status);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Cavab uğurla yoxlanıldı", null));
    }
}
