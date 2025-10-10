package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.HomeResponse;
import com.exam.examapp.service.interfaces.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Tag(name = "Home", description = "Home related endpoints")
public class HomeController {
    private final HomeService homeService;

    @GetMapping
    @Operation(
            summary = "Get home info",
            description = "Returns home page information"
    )
    public ResponseEntity<ApiResponse<HomeResponse>> home() {
        HomeResponse homeInfo = homeService.getHomeInfo();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Ev məlumatı uğurla alındı", homeInfo));
    }
}
