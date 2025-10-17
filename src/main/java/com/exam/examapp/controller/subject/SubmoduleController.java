package com.exam.examapp.controller.subject;

import com.exam.examapp.dto.request.subject.SubmoduleRequest;
import com.exam.examapp.dto.request.subject.SubmoduleUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.SubmoduleResponse;
import com.exam.examapp.model.exam.Submodule;
import com.exam.examapp.service.interfaces.subject.SubmoduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/v1/sub-module")
@Tag(name = "Submodule Management", description = "Endpoints for managing submodules under specific modules")
public class SubmoduleController {
    private final SubmoduleService submoduleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new submodule",
            description = "Allows an **ADMIN** to create a submodule by providing the parent module ID, name, and logo file."
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestPart
                                                    @Valid
                                                    SubmoduleRequest request,
                                                    @RequestPart
                                                    MultipartFile logo) {
        submoduleService.create(request, logo);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Alt modul uğurla yaradıldı",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get all submodules",
            description = "Retrieves a list of all submodules. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<Submodule>>> getAll() {
        List<Submodule> submodules = submoduleService.getAll();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Alt modullar uğurla əldə edildi",
                        submodules));
    }

    @GetMapping("/all-responses")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all submodule responses",
            description = "Retrieves a list of all submodule responses. Accessible by **ADMIN** roles."
    )
    public ResponseEntity<ApiResponse<List<SubmoduleResponse>>> getAllResponses() {
        List<SubmoduleResponse> submoduleResponses = submoduleService.getAllSubmoduleResponse();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Alt modullar və detallar uğurla əldə edildi",
                        submoduleResponses));
    }

    @GetMapping("/module")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get submodules by module",
            description = "Retrieves all submodules under a specific module ID. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<Submodule>>> getByModule(@RequestParam UUID moduleId) {
        List<Submodule> submodules = submoduleService.getAllByModule(moduleId);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Alt modullar uğurla əldə edildi",
                        submodules));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get submodule by ID",
            description = "Retrieves a submodule by its unique ID."
    )
    public ResponseEntity<ApiResponse<Submodule>> getById(@RequestParam UUID id) {
        Submodule submodule = submoduleService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Submodul uğurla əldə edildi",
                        submodule));
    }

    @GetMapping("/name")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get submodule by name",
            description = "Retrieves a submodule by its name."
    )
    public ResponseEntity<ApiResponse<Submodule>> getByName(@RequestParam
                                                            @NotBlank
                                                            @Schema(defaultValue = "Sub module name")
                                                            String name) {
        Submodule submodule = submoduleService.getByName(name);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Submodul uğurla əldə edildi",
                        submodule));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a submodule",
            description = "Allows an **ADMIN** to update a submodule by providing a request body and an optional logo file."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @Valid
                                                    SubmoduleUpdateRequest request,
                                                    @RequestPart
                                                    MultipartFile logo) {
        submoduleService.update(request, logo);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Alt modul uğurla yeniləndi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a submodule",
            description = "Allows an **ADMIN** to delete a submodule by its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam UUID id) {
        submoduleService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Alt modul uğurla silindi",
                        null));
    }
}
