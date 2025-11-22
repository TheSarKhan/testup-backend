package com.exam.examapp.controller;

import com.exam.examapp.dto.request.exam.AddExamTeacherRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.ExamTeacherResponse;
import com.exam.examapp.service.interfaces.exam.ExamTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam-teacher")
@Tag(name = "Exam Teacher", description = "Endpoints to manage exam-teacher assignments (ADMIN only)")
public class ExamTeacherController {
    private final ExamTeacherService examTeacherService;

    @PatchMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Assign teacher to exam",
            description = "Assigns a teacher to an exam. Accessible only by users with ADMIN role."
    )
    public ResponseEntity<ApiResponse<Void>> addExamTeacher(@RequestBody @Valid AddExamTeacherRequest request) {
        String response = examTeacherService.addExamTeacher(request);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, response, null));
    }

    @GetMapping("/get-info")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get exam info",
            description = "Retrieve exam details by exam UUID."
    )
    public ResponseEntity<ApiResponse<ExamTeacherResponse>> addExamTeacher(@RequestParam UUID examId) {
        ExamTeacherResponse examTeacher = examTeacherService.getExamTeacher(examId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Retrieve exam info", examTeacher));
    }

    @PatchMapping("/remove")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Remove teacher from exam",
            description = "Removes a teacher from an exam. Accessible only by users with ADMIN role."
    )
    public ResponseEntity<ApiResponse<Void>> removeExamTeacher(@RequestParam UUID examId,
                                                               @RequestParam UUID teacherId) {
        examTeacherService.removeExamTeacher(examId, teacherId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan Müəllim uğurla çıxarıldı", null));
    }
}
