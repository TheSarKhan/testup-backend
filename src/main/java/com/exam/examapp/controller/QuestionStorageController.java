package com.exam.examapp.controller;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.service.interfaces.question.QuestionStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question-storage")
@Tag(
        name = "Question Storage",
        description = "Operations for managing personal and admin question storage")
public class QuestionStorageController {
    private final QuestionStorageService questionStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
                ApiResponse.build(HttpStatus.CREATED, "Sual yaddaşa uğurla əlavə edildi", null));
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
                ApiResponse.build(HttpStatus.OK, "Suallar uğurla əldə edildi", questions));
    }

    @GetMapping("/my-filtered")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get My Filtered Storage",
            description =
                    "Retrieve filtered questions from personal storage by type, difficulty, topic, and number of questions")
    public ResponseEntity<ApiResponse<List<Question>>> getMyFilteredStorage(
            @RequestParam(required = false) QuestionType type,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) UUID topicId,
            @RequestParam(required = false) int numberOfQuestions) {
        List<Question> questions =
                questionStorageService.getQuestionsFromMyStorage(
                        type, difficulty, topicId, numberOfQuestions);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Suallar uğurla əldə edildi", questions));
    }

    @GetMapping("/my-by-subject")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get My Question Storage By Subject",
            description =
                    "Retrieve questions from personal storage by subject")
    public ResponseEntity<ApiResponse<List<Question>>> getMyStorageBySubject(
            @RequestParam UUID subjectId) {
        List<Question> questions =
                questionStorageService.getQuestionsFromMyStorage(subjectId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Suallar uğurla əldə edildi", questions));
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
                ApiResponse.build(HttpStatus.OK, "Suallar uğurla əldə edildi", questions));
    }

    @GetMapping("/admin-filtered")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get Admin Filtered Storage",
            description =
                    "Retrieve filtered questions from admin storage by type, difficulty, topic, and number of questions")
    public ResponseEntity<ApiResponse<List<Question>>> getAdminFilteredStorage(
            @RequestParam(required = false) QuestionType type,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) UUID topicId,
            @RequestParam(required = false) int numberOfQuestions) {
        List<Question> questions =
                questionStorageService.getQuestionFromAdminStorage(
                        type, difficulty, topicId, numberOfQuestions);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Suallar uğurla əldə edildi", questions));
    }

    @GetMapping("/teachers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get teachers with question storage",
            description = "Retrieves a list of teachers who have question storage. Accessible only by ADMIN."
    )
    public ResponseEntity<ApiResponse<List<User>>> getTeachers() {
        List<User> response = questionStorageService.getTeachersHasQuestionStorage();
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Müəllimlər uğurla tapıldı", response));
    }

    @GetMapping("/by-teacher")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get questions by teacher",
            description = "Retrieves all questions created by the specified teacher. Accessible only by ADMIN."
    )
    public ResponseEntity<ApiResponse<List<Question>>> getQuestionByTeacher(@RequestParam UUID teacherId) {
        List<Question> response = questionStorageService.getQuestionsByTeacherId(teacherId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Suallar uğurla əldə edildi", response));
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
                ApiResponse.build(HttpStatus.OK, "Sual uğurla yeniləndi", null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Delete Question", description = "Delete a question from storage by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionStorage(@RequestParam UUID questionId) {
        questionStorageService.removeQuestionsFromStorage(questionId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.NO_CONTENT, "Sual uğurla silindi", null));
    }
}
