package com.exam.examapp.controller;

import com.exam.examapp.dto.request.MultiEmailRequest;
import com.exam.examapp.dto.request.MultiNotificationRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.security.service.interfaces.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
@Tag(name = "Email Management", description = "Endpoints for managing and retrieving emails")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/multi")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Send emails",
            description = "Allows an **ADMIN** to send new emails by providing necessary details."
    )
    public ResponseEntity<ApiResponse<Void>> sendNotifications(@RequestBody MultiEmailRequest request) {
        emailService.sendEmailToAll(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "E-poçtlar uğurla göndərildi",
                        null));
    }
}
