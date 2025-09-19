package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.HeaderResponse;
import com.exam.examapp.service.interfaces.HeaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/header")
@Tag(name = "Header",
        description = "Operations related to application header information")
public class HeaderController {
    private final HeaderService headerService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Header Info",
            description = "Retrieve application header information for authenticated users")
    public ResponseEntity<ApiResponse<HeaderResponse>> getHeader() {
        HeaderResponse headerInfo = headerService.getHeaderInfo();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Header info received successfully", headerInfo));
    }
}
