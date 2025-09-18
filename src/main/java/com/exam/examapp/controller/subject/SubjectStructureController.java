package com.exam.examapp.controller.subject;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
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
@RequestMapping("/api/v1/subject-structure")
@Tag(
        name = "Subject Structures Management",
        description = "Endpoints for managing subject structures with logo upload support"
)
public class SubjectStructureController {
    private final SubjectStructureService subjectStructureService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new subject structure",
            description = "Allows an **ADMIN** to create a new subject structure by providing request data and uploading a logo file."
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody
                                                    @Valid
                                                    SubjectStructureRequest request) {
        subjectStructureService.create(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Subject structure created successfully",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Retrieve all subject structures",
            description = "Fetches a list of all subject structures. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<SubjectStructure>>> getAll() {
        List<SubjectStructure> structures = subjectStructureService.getAll();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject structures received successfully",
                        structures));
    }

    @GetMapping("/subject")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get subject structures by Subject ID",
            description = "Retrieve all subject structures linked to a specific subject. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<SubjectStructure>>> getBySubject(@RequestParam
                                                                            UUID subjectId) {
        List<SubjectStructure> structures = subjectStructureService.getBySubjectId(subjectId);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject structures received successfully",
                        structures));
    }

    @GetMapping("/submodule")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get subject structures by Submodule ID",
            description = "Retrieve all subject structures linked to a specific submodule. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<SubjectStructure>>> getBySubmodule(@RequestParam UUID submoduleId) {
        List<SubjectStructure> structures = subjectStructureService.getBySubmoduleId(submoduleId);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject structures received successfully",
                        structures));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get subject structure by ID",
            description = "Retrieve a specific subject structure by its unique ID. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<SubjectStructure>> getById(@RequestParam
                                                                 UUID id) {
        SubjectStructure subjectStructure = subjectStructureService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject structure received successfully",
                        subjectStructure));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing subject structure",
            description = "Allows an **ADMIN** to update a subject structure's details and logo by providing its ID and update request data."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestBody
                                                    @Valid
                                                    SubjectStructureUpdateRequest request) {
        subjectStructureService.update(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Subject structure updated successfully",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a subject structure",
            description = "Allows an **ADMIN** to delete a subject structure by its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    UUID id) {
        subjectStructureService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Subject structure deleted successfully",
                        null));
    }
}
