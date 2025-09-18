package com.exam.examapp.controller;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.service.interfaces.QuestionStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question-storage")
@Tag(
    name = "Question Storage",
    description = "Operations for managing personal and admin question storage")
public class QuestionStorageController {
  private final QuestionStorageService questionStorageService;

  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Create Question",
      description =
          "Add a new question with optional files (titles, variants, numbers, sounds) to the storage")
  public ResponseEntity<ApiResponse<Void>> createQuestionStorage(
      @RequestPart @Valid QuestionRequest request,
      @RequestPart(required = false) List<MultipartFile> titles,
      @RequestPart(required = false) List<MultipartFile> variantPictures,
      @RequestPart(required = false) List<MultipartFile> numberPictures,
      @RequestPart(required = false) List<MultipartFile> sounds) {
    questionStorageService.addQuestionsToStorage(
        request, titles, variantPictures, numberPictures, sounds);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.CREATED, "Question added to storage successfully", null));
  }

  @GetMapping("/my-storage")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get My Storage",
      description = "Retrieve all questions from the personal storage of the logged-in user")
  public ResponseEntity<ApiResponse<List<Question>>> getMyStorage() {
    List<Question> questions = questionStorageService.getAllQuestionsFromMyStorage();
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Questions retrieved successfully", questions));
  }

  @GetMapping("/my-filtered")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get My Filtered Storage",
      description =
          "Retrieve filtered questions from personal storage by type, difficulty, topic, and number of questions")
  public ResponseEntity<ApiResponse<List<Question>>> getMyFilteredStorage(
      @RequestParam QuestionType type,
      @RequestParam Difficulty difficulty,
      @RequestParam UUID topicId,
      @RequestParam int numberOfQuestions) {
    List<Question> questions =
        questionStorageService.getQuestionsFromMyStorage(
            type, difficulty, topicId, numberOfQuestions);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Questions retrieved successfully", questions));
  }

  @GetMapping("/admin-storage")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get Admin Storage",
      description = "Retrieve all questions from the global admin storage")
  public ResponseEntity<ApiResponse<List<Question>>> getAdminStorage() {
    List<Question> questions = questionStorageService.getAllQuestionsFromAdminStorage();
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Questions retrieved successfully", questions));
  }

  @GetMapping("/admin-filtered")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Get Admin Filtered Storage",
      description =
          "Retrieve filtered questions from admin storage by type, difficulty, topic, and number of questions")
  public ResponseEntity<ApiResponse<List<Question>>> getAdminFilteredStorage(
      @RequestParam QuestionType type,
      @RequestParam Difficulty difficulty,
      @RequestParam UUID topicId,
      @RequestParam int numberOfQuestions) {
    List<Question> questions =
        questionStorageService.getQuestionFromAdminStorage(
            type, difficulty, topicId, numberOfQuestions);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Questions retrieved successfully", questions));
  }

  @PatchMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(
      summary = "Update Question",
      description =
          "Update an existing question with new data and optional files (titles, variants, numbers, sounds)")
  public ResponseEntity<ApiResponse<Void>> updateQuestionStorage(
      @RequestPart @Valid QuestionUpdateRequest request,
      @RequestPart(required = false) List<MultipartFile> titles,
      @RequestPart(required = false) List<MultipartFile> variantPictures,
      @RequestPart(required = false) List<MultipartFile> numberPictures,
      @RequestPart(required = false) List<MultipartFile> sounds) {
    questionStorageService.updateQuestionInStorage(
        request, titles, variantPictures, numberPictures, sounds);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.OK, "Question updated successfully", null));
  }

  @DeleteMapping
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  @Operation(summary = "Delete Question", description = "Delete a question from storage by its ID")
  public ResponseEntity<ApiResponse<Void>> deleteQuestionStorage(@RequestParam UUID questionId) {
    questionStorageService.removeQuestionsFromStorage(questionId);
    return ResponseEntity.ok(
        ApiResponse.build(HttpStatus.NO_CONTENT, "Question deleted successfully", null));
  }
}
