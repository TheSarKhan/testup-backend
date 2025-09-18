package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.service.interfaces.information.InitializeInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/init")
@Tag(
        name = "Initialization Management",
        description = "Endpoints for initializing base data (Advertisement, Superiority, Media Content). " +
                " If data already exists in the database, these endpoints will **not perform any operation**."
)
public class InitController {
    private final InitializeInformationService initializeInformationService;

    @PostMapping(value = "/advertisement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Initialize advertisements",
            description = "Uploads a list of advertisement images and inserts them into the database. " +
                    "If advertisements already exist in the DB, this operation will be skipped."
    )
    public ResponseEntity<ApiResponse<Void>> initAdvertisement(@RequestPart List<MultipartFile> images){
        initializeInformationService.initializeAdvertisement(images);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Advertisement initialized successfully",
                null));
    }

    @PostMapping(value = "/superiority", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "beareAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Initialize superiorities",
            description = "Uploads a list of superiority icons and inserts them into the database. " +
                    "If superiorities already exist in the DB, this operation will be skipped."
    )
    public ResponseEntity<ApiResponse<Void>> initSuperiority(@RequestPart List<MultipartFile> icons){
        initializeInformationService.initializeSuperiority(icons);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Superiority initialized successfully",
                null));
    }

    @PostMapping(value = "/media-content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Initialize media content",
            description = "Uploads a list of media content images and inserts them into the database. " +
                    "If media content already exists in the DB, this operation will be skipped."
    )
    public ResponseEntity<ApiResponse<Void>> initMediaContent(@RequestPart List<MultipartFile> images){
        initializeInformationService.initializeMediaContent(images);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Media Content initialized successfully",
                null));
    }
}
