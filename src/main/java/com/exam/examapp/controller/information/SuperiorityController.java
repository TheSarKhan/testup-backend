package com.exam.examapp.controller.information;

import com.exam.examapp.dto.request.information.SuperiorityRequest;
import com.exam.examapp.dto.request.information.SuperiorityUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.model.information.Superiority;
import com.exam.examapp.service.interfaces.information.SuperiorityService;
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
@RequestMapping("/api/v1/superiority")
@Tag(name = "Superiority Management", description = "Endpoints for managing superiority records such as advantages and features")
public class SuperiorityController {
    private final SuperiorityService superiorityService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new superiority",
            description = "Allows an **ADMIN** to create a new superiority by providing details in JSON and uploading an icon file."
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestPart
                                                    @Valid
                                                    SuperiorityRequest request,
                                                    @RequestPart
                                                    MultipartFile icon) {
        superiorityService.createSuperiority(request, icon);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Üstünlük uğurla yaradıldı",
                        null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all superiority",
            description = "Fetches a list of all available superiority."
    )
    public ResponseEntity<ApiResponse<List<Superiority>>> getAll() {
        List<Superiority> allSuperiority = superiorityService.getAllSuperiority();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Üstünlük uğurla əldə edildi",
                        allSuperiority));
    }

    @GetMapping("/type")
    @Operation(
            summary = "Get superiority by type",
            description = "Retrieve a list of superiority filtered by superiority type."
    )
    public ResponseEntity<ApiResponse<List<Superiority>>> getByType(@RequestParam
                                                                    @NotNull
                                                                    SuperiorityType type) {
        List<Superiority> allSuperiority = superiorityService.getSuperiorityBySuperiorityType(type);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Üstünlük uğurla əldə edildi",
                        allSuperiority));
    }

    @GetMapping("/id")
    @Operation(
            summary = "Get superiority by ID",
            description = "Retrieve a specific superiority by its unique ID."
    )
    public ResponseEntity<ApiResponse<Superiority>> getById(@RequestParam
                                                            @NotNull
                                                            UUID id) {
        Superiority superiority = superiorityService.getSuperiorityById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Üstünlük uğurla əldə edildi",
                        superiority));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing superiority",
            description = "Allows an **ADMIN** to update a superiority by providing updated details and optionally a new icon file."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @Valid
                                                    SuperiorityUpdateRequest request,
                                                    @RequestPart(required = false)
                                                    MultipartFile icon) {
        superiorityService.updateSuperiority(request, icon);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Üstünlük uğurla yeniləndi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a superiority",
            description = "Allows an **ADMIN** to delete a superiority by providing its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        superiorityService.deleteSuperiority(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Üstünlük uğurla silindi",
                        null));
    }
}
