package com.exam.examapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
    @NotBlank @Size(min = 5, max = 50) String fullName,
    String phoneNumber,
    @Email @Size(min = 5, max = 50) String email) {}
