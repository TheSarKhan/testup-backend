package com.exam.examapp.controller;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.service.interfaces.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question")
@Tag(name = "Questions", description = "Operations related to question management")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Question", description = "Allows ADMIN to create a new question with optional media files (titles, variant images, numbers, sounds)")
    public ResponseEntity<ApiResponse<Void>> create(@RequestPart
                                                    @Valid
                                                    QuestionRequest request,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> titles,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> variantPictures,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> numberPictures,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> sounds) {
        questionService.save(request, titles, variantPictures, numberPictures, sounds);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Question created successfully",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get Questions", description = "Retrieve all questions with optional filtering by difficulty, type, and topic")
    public ResponseEntity<ApiResponse<List<Question>>> get(@RequestParam(required = false)
                                                           Difficulty difficulty,
                                                           @RequestParam(required = false)
                                                           QuestionType questionType,
                                                           @RequestParam(required = false)
                                                           UUID topicId) {
        List<Question> questions = questionService.getFilteredQuestions(difficulty, questionType, topicId);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Questions received successfully",
                        questions));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get Question by ID", description = "Retrieve a question by its ID")
    public ResponseEntity<ApiResponse<Question>> getById(@RequestParam
                                                         @NotNull
                                                         UUID id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Question received successfully",
                        question));
    }


    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Question", description = "Allows ADMIN to update an existing question with new details and optional media files")
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @Valid
                                                    QuestionUpdateRequest request,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> titles,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> variantPictures,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> numberPictures,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> sounds) {
        questionService.update(request, titles, variantPictures, numberPictures, sounds);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Question updated successfully",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Question", description = "Allows ADMIN to delete a question by ID")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        questionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Question deleted successfully",
                        null));
    }
}
