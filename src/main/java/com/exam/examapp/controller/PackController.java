package com.exam.examapp.controller;

import com.exam.examapp.dto.request.PackRequest;
import com.exam.examapp.dto.request.PackUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.Pack;
import com.exam.examapp.service.interfaces.PackService;
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
@RequestMapping("/api/v1/pack")
@Tag(name = "Pack Management", description = "Endpoints for managing packs")
public class PackController {
    private final PackService packService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new pack",
            description = "Allows an **ADMIN** to create a new pack by providing necessary details."
    )
    public ResponseEntity<ApiResponse<Void>> createPack(@RequestBody
                                                        @Valid
                                                        PackRequest request) {
        packService.createPack(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Paket uğurla yaradıldı",
                        null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all packs",
            description = "Fetches a list of all available packs."
    )
    public ResponseEntity<ApiResponse<List<Pack>>> getAll() {
        List<Pack> allPacks = packService.getAllPacks();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Paketlər uğurla alındı",
                        allPacks));
    }

    @GetMapping("/id")
    @Operation(
            summary = "Get pack by ID",
            description = "Retrieve a specific pack by providing its unique ID."
    )
    public ResponseEntity<ApiResponse<Pack>> getById(@RequestParam
                                                     @NotNull
                                                     UUID id) {
        Pack pack = packService.getPackById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Paket uğurla alındı",
                        pack));
    }

    @GetMapping("/name")
    @Operation(
            summary = "Get pack by name",
            description = "Retrieve a specific pack by providing its name."
    )
    public ResponseEntity<ApiResponse<Pack>> getByName(@RequestParam
                                                       @NotBlank
                                                       @Schema(defaultValue = "Pack Name")
                                                       String name) {
        Pack pack = packService.getPackByName(name);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Paket uğurla alındı",
                        pack));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing pack",
            description = "Allows an **ADMIN** to update a pack by providing its ID and new details."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestBody
                                                    @Valid
                                                    PackUpdateRequest request) {
        packService.updatePack(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Paket uğurla yeniləndi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a pack",
            description = "Allows an **ADMIN** to delete a pack by its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        packService.deletePack(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Paket uğurla silindi",
                        null));
    }
}
