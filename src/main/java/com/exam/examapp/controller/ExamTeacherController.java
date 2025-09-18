package com.exam.examapp.controller;

import com.exam.examapp.dto.request.AddExamTeacherRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.service.interfaces.ExamTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam-teacher")
@Tag(name = "Exam Teacher", description = "Endpoints to manage exam-teacher assignments (ADMIN only)")
public class ExamTeacherController {
    private final ExamTeacherService examTeacherService;

    @GetMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Assign teacher to exam",
            description = "Assigns a teacher to an exam. Accessible only by users with ADMIN role."
    )
    public ResponseEntity<ApiResponse<Void>> addExamTeacher(@RequestParam @Valid AddExamTeacherRequest request) {
        String response = examTeacherService.addExamTeacher(request);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, response, null));
    }

    @GetMapping("/remove")
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
                ApiResponse.build(HttpStatus.OK, "Exam Teacher removed successfully", null));
    }
}
