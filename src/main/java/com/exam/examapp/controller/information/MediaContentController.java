package com.exam.examapp.controller.information;

import com.exam.examapp.dto.request.information.MediaContentRequest;
import com.exam.examapp.dto.request.information.MediaContentUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.information.MediaContentResponse;
import com.exam.examapp.model.enums.PageType;
import com.exam.examapp.service.interfaces.information.MediaContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/media-content")
@Tag(name = "Media Content Management", description = "Endpoints for managing media contents like banners, sliders, and other page-related content")
public class MediaContentController {
    private final MediaContentService mediaContentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create media content",
            description = "Allows an **ADMIN** to create new media content (e.g., banner, slider) by providing required details."
    )
    public ResponseEntity<ApiResponse<Void>> create(@RequestPart
                                                    @Valid
                                                    MediaContentRequest request,
                                                    @RequestPart
                                                    MultipartFile image) {
        mediaContentService.saveMediaContent(request, image);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Media Məzmun Uğurla Yaradıldı",
                        null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all media contents",
            description = "Fetches a list of all available media contents."
    )
    public ResponseEntity<ApiResponse<List<MediaContentResponse>>> getAll() {
        List<MediaContentResponse> allMediaContent = mediaContentService.getAllMediaContent();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Media Məzmunu Uğurla Alındı",
                        allMediaContent));
    }

    @GetMapping("/page-type")
    @Operation(
            summary = "Get media contents by page type",
            description = "Retrieve media contents based on the given page type (e.g., HOME, ABOUT, CONTACT)."
    )
    public ResponseEntity<ApiResponse<List<MediaContentResponse>>> getByPageType(@RequestParam
                                                                                     @NotNull
                                                                                     @Schema(defaultValue = "HOME_BANNER")
                                                                                     PageType pageType) {
        List<MediaContentResponse> mediaContents = mediaContentService.getMediaContentsByPageType(pageType);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Media Məzmunu Uğurla Alındı",
                        mediaContents));
    }

    @GetMapping("/id")
    @Operation(
            summary = "Get media content by ID",
            description = "Retrieve a specific media content by providing its unique ID."
    )
    public ResponseEntity<ApiResponse<MediaContentResponse>> getById(@RequestParam
                                                                         @NotNull
                                                                         UUID id) {
        MediaContentResponse mediaContent = mediaContentService.getMediaContentById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Media Məzmunu Uğurla Alındı",
                        mediaContent));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update media content",
            description = "Allows an **ADMIN** to update an existing media content by providing updated details."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @Valid
                                                    MediaContentUpdateRequest request,
                                                    @RequestPart(required = false)
                                                    MultipartFile image) {
        mediaContentService.updateMediaContent(request, image);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Media Məzmun Uğurla Yeniləndi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete media content",
            description = "Allows an **ADMIN** to delete media content by providing its ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam
                                                    @NotNull
                                                    UUID id) {
        mediaContentService.deleteMediaContent(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Media Məzmunu Uğurla Silindi",
                        null));
    }
}
