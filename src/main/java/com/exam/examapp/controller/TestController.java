package com.exam.examapp.controller;

import com.exam.examapp.DailyTask;
import com.exam.examapp.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
@Tag(name = "Test Management", description = "Endpoints for managing tests")
public class TestController {
    private final DailyTask dailyTask;

    @GetMapping("/hello")
    @Operation(summary = "Hello World", description = "Returns hello world message.")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Test işi", "Salam Dünya"));
    }

    @GetMapping("/hello-message")
    @Operation(summary = "Hello World", description = "Returns hello world message.")
    public ResponseEntity<ApiResponse<String>> helloUrl(@RequestParam String message) {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "This is your message", message));
    }

    @GetMapping("/trigger-reset-teacher-info")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "PLEASE DON'T SEND REQUEST")
    public ResponseEntity<ApiResponse<String>> resetTeacherInfo() {
        dailyTask.resetTeacherInfo();
        log.info("Reset teacher info");
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Daily jobs triggered", "OK"));
    }

    @GetMapping("/trigger-payment-reminder")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "PLEASE DON'T SEND REQUEST")
    public ResponseEntity<ApiResponse<String>> trigger() {
        dailyTask.sendPaymentReminders();
        log.info("Send payment reminders");
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Daily jobs triggered", "OK"));
    }
}
