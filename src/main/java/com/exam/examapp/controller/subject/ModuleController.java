package com.exam.examapp.controller.subject;

import com.exam.examapp.dto.request.ModuleUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.ModuleResponse;
import com.exam.examapp.model.exam.Module;
import com.exam.examapp.service.interfaces.subject.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/api/v1/module")
@Tag(name = "Module Management", description = "Endpoints for managing modules with logo upload support")
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new module",
            description = "Allows an **ADMIN** to create a new module by providing a module name and uploading a logo file."
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestPart
                                                    @NotBlank
                                                    @Schema(defaultValue = "Module name")
                                                    String moduleName,
                                                    @RequestPart
                                                    MultipartFile logo) {
        moduleService.createModule(moduleName, logo);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Modul uğurla yaradıldı",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Retrieve all modules",
            description = "Fetches a list of all modules. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<List<Module>>> getAll() {
        List<Module> allModules = moduleService.getAllModules();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Modullar uğurla əldə edildi",
                        allModules));
    }

    @GetMapping("/all-response")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Retrieve all module responses",
            description = "Fetches a list of all module responses. Accessible by **ADMIN** roles."
    )
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAllResponses() {
        List<ModuleResponse> allModuleResponses = moduleService.getAllModulesResponse();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Modullar və detallar uğurla əldə edildi",
                        allModuleResponses));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get module by ID",
            description = "Retrieve a specific module by its unique ID. Accessible by **ADMIN** and **TEACHER** roles."
    )
    public ResponseEntity<ApiResponse<Module>> getById(@RequestParam
                                                       @NotNull
                                                       UUID id) {
        Module module = moduleService.getModuleById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Modul uğurla əldə edildi",
                        module));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing module",
            description = "Allows an **ADMIN** to update a module's name and logo by providing its ID."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart @Valid ModuleUpdateRequest request,
                                                    @RequestPart(required = false) MultipartFile logo) {
        moduleService.updateModule(request, logo);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Modul uğurla yeniləndi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a module",
            description = "Allows an **ADMIN** to delete a module by its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Modul uğurla silindi",
                        null));
    }
}
