package com.exam.examapp.security.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.security.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth2")
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> getLogin(@RequestParam String id) {
        TokenResponse token = oAuth2Service.getToken(id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Login successful",
                        token));
    }
}
