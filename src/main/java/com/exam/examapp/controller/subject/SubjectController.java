package com.exam.examapp.controller.subject;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/v1/subject")
@Tag(name = "Subject Management", description = "Endpoints for managing subjects with logo support")
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new subject",
            description = "Allows an **ADMIN** to create a subject by providing its name and uploading a logo file."
    )
    public ResponseEntity<ApiResponse<Void>> save(@RequestPart
                                                  @NotBlank
                                                  @Schema(defaultValue = "Subject name")
                                                  String name,
                                                  @RequestPart
                                                  MultipartFile logo) {
        subjectService.save(name, logo);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Subject created successfully",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get all subjects",
            description = "Retrieves a list of all subjects. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<Subject>>> findAll() {
        List<Subject> subjects = subjectService.getAll();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subjects retrieved successfully",
                        subjects));
    }

    @GetMapping("/name")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get subject by name",
            description = "Retrieves a subject by its name. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<Subject>> findByName(@RequestParam
                                                           @NotBlank
                                                           @Schema(defaultValue = "Subject name")
                                                           String name) {
        Subject subject = subjectService.getByName(name);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject received successfully",
                        subject));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get subject by ID",
            description = "Retrieves a subject using its unique ID. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<Subject>> findById(@RequestParam
                                                         @NotNull
                                                         UUID id) {
        Subject subject = subjectService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject received successfully",
                        subject));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a subject",
            description = "Allows an **ADMIN** to update the name and logo of an existing subject by its ID."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @NotNull
                                                    UUID id,
                                                    @RequestPart
                                                    @NotBlank
                                                    @Schema(defaultValue = "Subject name")
                                                    String name,
                                                    @RequestPart
                                                    MultipartFile logo) {
        subjectService.update(id, name, logo);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject updated successfully",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a subject",
            description = "Allows an **ADMIN** to delete a subject by its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        subjectService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Subject deleted successfully",
                        null));
    }
}
