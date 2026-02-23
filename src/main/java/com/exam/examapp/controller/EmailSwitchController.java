package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.enums.Email;
import com.exam.examapp.service.EmailSwitchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-switch")
@SecurityRequirement(name = "bearerAuth")
public class EmailSwitchController {
    private final EmailSwitchService emailSwitchService;

    @GetMapping("/current-service")
    public ResponseEntity<ApiResponse<Email>> getCurrentEmailService() {
        Email email = emailSwitchService.getCurrentEmailService();
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Email service",
                email));
    }

    @PutMapping("/change-service")
    public ResponseEntity<ApiResponse<Void>> changeEmailService(@RequestParam Email email) {
        emailSwitchService.chanceEmailService(email);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Email service deyisdirildi.", null));
    }
}
