package com.exam.examapp.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequest(@Email
                           @Schema(defaultValue = "example@gmail.com")
                           String email,
                           @Size(min = 8)
                           @Schema(defaultValue = "Example123")
                           String password) {
}
