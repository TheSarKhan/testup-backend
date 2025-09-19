package com.exam.examapp.controller;

import com.exam.examapp.dto.request.ExamTemplateRequest;
import com.exam.examapp.dto.request.ExamTemplateUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.exam.ExamTemplate;
import com.exam.examapp.service.interfaces.exam.ExamTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam-template")
@Tag(name = "Exam Template", description = "Operations related to Exam Templates management")
public class ExamTemplateController {
  private final ExamTemplateService examTemplateService;

  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Create Exam Template",
      description = "Allows ADMIN to create a new exam template")
  public ResponseEntity<ApiResponse<Void>> createExamTemplate(
      @RequestBody @Valid ExamTemplateRequest request) {
    examTemplateService.createExamTemplate(request);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.CREATED, "Exam Template created successfully", null));
  }

  @GetMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get all Exam Templates",
      description = "Allows ADMIN and TEACHER to view all exam templates")
  public ResponseEntity<ApiResponse<List<ExamTemplate>>> getAllExamTemplates() {
    List<ExamTemplate> allExamTemplates = examTemplateService.getAllExamTemplates();
    return ResponseEntity.ok(
        ApiResponse.build(
            HttpStatus.OK, "Exam Templates retrieved successfully", allExamTemplates));
  }

  @GetMapping("/submodule")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get Exam Templates by Submodule",
      description = "Retrieve all exam templates by submodule ID (ADMIN, TEACHER only)")
  public ResponseEntity<ApiResponse<List<ExamTemplate>>> getAllExamTemplatesBySubmoduleId(
      @RequestParam UUID submoduleId) {
    List<ExamTemplate> examTemplatesBySubModuleId =
        examTemplateService.getExamTemplatesBySubModuleId(submoduleId);
    return ResponseEntity.ok(
        ApiResponse.build(
            HttpStatus.OK, "Exam Templates retrieved successfully", examTemplatesBySubModuleId));
  }

  @GetMapping("/id")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get Exam Template by ID",
      description = "Retrieve exam template by unique ID (ADMIN, TEACHER only)")
  public ResponseEntity<ApiResponse<ExamTemplate>> getExamTemplateById(@RequestParam UUID id) {
    ExamTemplate examTemplate = examTemplateService.getExamTemplateById(id);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Exam Template retrieved successfully", examTemplate));
  }

  @PutMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Update Exam Template",
      description = "Allows ADMIN to update an existing exam template")
  public ResponseEntity<ApiResponse<Void>> updateExamTemplate(
      @RequestBody @Valid ExamTemplateUpdateRequest request) {
    examTemplateService.updateExamTemplate(request);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Exam Template updated successfully", null));
  }

  @DeleteMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Delete Exam Template",
      description = "Allows ADMIN to delete an existing exam template by ID")
  public ResponseEntity<ApiResponse<Void>> deleteExamTemplate(@RequestParam UUID id) {
    examTemplateService.deleteExamTemplate(id);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.NO_CONTENT, "Exam Template deleted successfully", null));
  }
}
