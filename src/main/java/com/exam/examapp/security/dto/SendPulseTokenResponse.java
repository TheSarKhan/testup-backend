package com.exam.examapp.security.dto;

public record SendPulseTokenResponse(
        String access_token,
        String token_type,
        Integer expires_in
) {}
