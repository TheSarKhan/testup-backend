package com.exam.examapp.controller;

import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
@Tag(name = "Test Management", description = "Endpoints for managing tests")
public class TestController {
    private final TestService testService;

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

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Statistics", description = "Retrieve statistics.")
    public ResponseEntity<ApiResponse<AdminStatisticsResponse>> getStatistics() {
        AdminStatisticsResponse adminStatistics = testService.getAdminStatistics();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Statistika uğurla əldə edildi", adminStatistics));
    }
}
