package com.exam.examapp.controller.information;

import com.exam.examapp.dto.request.information.AdvertisementRequest;
import com.exam.examapp.dto.request.information.AdvertisementUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.information.Advertisement;
import com.exam.examapp.service.interfaces.information.AdvertisementService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/advertisement")
@Tag(name = "Advertisement Management", description = "Endpoints for managing advertisements")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new advertisement",
            description = "Allows an **ADMIN** to create a new advertisement by providing its details."
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestPart
                                                    @Valid
                                                    AdvertisementRequest request,
                                                    @RequestPart
                                                    MultipartFile image) {
        advertisementService.saveAdvertisement(request, image);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Reklam uğurla yaradıldı",
                        null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all advertisements",
            description = "Fetches a list of all available advertisements."
    )
    public ResponseEntity<ApiResponse<List<Advertisement>>> getAll() {
        List<Advertisement> allAdvertisements = advertisementService.getAdvertisements();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Reklamlar uğurla qəbul edildi",
                        allAdvertisements));
    }

    @GetMapping("/id")
    @Operation(
            summary = "Get an advertisement by ID",
            description = "Retrieve a specific advertisement by providing its unique ID."
    )
    public ResponseEntity<ApiResponse<Advertisement>> getById(@RequestParam
                                                              @NotNull
                                                              UUID id) {
        Advertisement advertisement = advertisementService.getAdvertisementById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Reklamlar uğurla qəbul edildi",
                        advertisement));
    }

    @GetMapping("/title")
    @Operation(
            summary = "Get an advertisement by title",
            description = "Retrieve a specific advertisement by providing its title."
    )
    public ResponseEntity<ApiResponse<Advertisement>> getByTitle(@RequestParam
                                                                 @NotBlank
                                                                 @Schema(defaultValue = "Advertisement Title")
                                                                 String title) {
        Advertisement advertisement = advertisementService.getAdvertisementByTitle(title);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Reklamlar uğurla qəbul edildi",
                        advertisement));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing advertisement",
            description = "Allows an **ADMIN** to update an advertisement by providing updated details."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @Valid
                                                    AdvertisementUpdateRequest request,
                                                    @RequestPart
                                                    MultipartFile image) {
        advertisementService.updateAdvertisement(request, image);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Reklamlar uğurla qəbul edildi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete an advertisement",
            description = "Allows an **ADMIN** to delete an advertisement by its ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Reklamlar uğurla qəbul edildi",
                        null));
    }
}
