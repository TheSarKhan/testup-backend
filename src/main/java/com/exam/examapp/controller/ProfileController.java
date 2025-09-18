package com.exam.examapp.controller;

import com.exam.examapp.dto.request.ProfileUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.ProfileInfoResponse;
import com.exam.examapp.dto.response.ProfileSettingsResponse;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.service.interfaces.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Profile related endpoints")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/info")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get profile info", description = "Returns user profile info")
    public ResponseEntity<ApiResponse<ProfileInfoResponse>> getProfileInfo() {
        ProfileInfoResponse profileInfo = profileService.getProfileInfo();
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Profile info",
                profileInfo
        ));
    }

    @GetMapping("/setting")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get profile settings", description = "Returns user profile settings")
    public ResponseEntity<ApiResponse<ProfileSettingsResponse>> getProfileSettings() {
        ProfileSettingsResponse profileSettings = profileService.getProfileSettings();
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Profile settings",
                profileSettings
        ));
    }

    @GetMapping("/teacher-info")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Get teacher info",
            description = "Returns teacher-specific information. Accessible only by users with TEACHER role"
    )
    public ResponseEntity<ApiResponse<TeacherInfo>> getTeacherInfo() {
        TeacherInfo teacherInfo = profileService.getTeacherInfo();
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Teacher info",
                teacherInfo
        ));
    }

    @PatchMapping("/update-settings")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update profile settings", description = "Updates user profile settings")
    public ResponseEntity<ApiResponse<TokenResponse>> updateSettings(@RequestBody @Valid ProfileUpdateRequest request) {
        TokenResponse tokenResponse = profileService.updateProfileInfo(request);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Profile settings updated successfully",
                tokenResponse
        ));
    }

    @PatchMapping(value = "/update-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update profile picture", description = "Updates user profile picture")
    public ResponseEntity<ApiResponse<Void>> updateSettings(@RequestParam(required = false) MultipartFile image) {
        profileService.updateProfilePicture(image);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Profile picture updated successfully",
                null
        ));
    }
}