package com.exam.examapp.controller.subject;

import com.exam.examapp.dto.request.subject.TopicUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.service.interfaces.subject.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topic")
@Tag(name = "Topic Management", description = "Endpoints for managing topics under subjects")
public class TopicController {
    private final TopicService topicService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new topic",
            description = "Allows **ADMIN** to create a new topic under a subject"
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestParam UUID subjectId,
                                                    @RequestParam
                                                    @NotBlank
                                                    @Schema(defaultValue = "Topic name")
                                                    String name) {
        topicService.save(subjectId, name);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Topic created successfully",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get all topics",
            description = "Retrieves all topics. Accessible by **ADMIN** and **TEACHER**."
    )
    public ResponseEntity<ApiResponse<List<Topic>>> getAll() {
        List<Topic> topics = topicService.getAll();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Topics retrieved successfully",
                        topics));
    }

    @GetMapping("/subject")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get topics by subject ID",
            description = "Retrieves all topics belonging to a specific subject"
    )
    public ResponseEntity<ApiResponse<List<Topic>>> getAllBySubject(@RequestParam UUID subjectId) {
        List<Topic> topics = topicService.getAllBySubjectId(subjectId);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Topics retrieved successfully",
                        topics));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get topic by ID",
            description = "Retrieves a topic using its unique ID"
    )
    public ResponseEntity<ApiResponse<Topic>> getById(@RequestParam UUID id) {
        Topic topic = topicService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Topic retrieved successfully",
                        topic));
    }

    @GetMapping("/name")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get topic by name",
            description = "Retrieves a topic using its name"
    )
    public ResponseEntity<ApiResponse<Topic>> getByName(@RequestParam
                                                        @NotBlank
                                                        @Schema(defaultValue = "Topic name")
                                                        String name) {
        Topic topic = topicService.getByName(name);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Topic retrieved successfully",
                        topic));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update topic",
            description = "Allows **ADMIN** to update topic information"
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestBody
                                                    @Valid
                                                    TopicUpdateRequest request) {
        topicService.update(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Topic updated successfully",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete topic",
            description = "Allows **ADMIN** to delete a topic by ID"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam UUID id) {
        topicService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Topic deleted successfully",
                        null));
    }
}
