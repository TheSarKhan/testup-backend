package com.exam.examapp.security.dto.request;

import com.exam.examapp.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Size(min = 5, max = 50)
                              @Schema(defaultValue = "Full Name")
                              String fullName,
                              @NotBlank @Size(min = 8)
                              @Schema(defaultValue = "Example123")
                              String password,
                              @NotBlank @Size(min = 6, max = 50)
                              @Schema(defaultValue = "example@gmail.com")
                              String email,
                              @NotBlank @Size(min = 12)
                              @Schema(defaultValue = "+994777777777")
                              String phoneNumber,
                              @NotNull
                              @Schema(defaultValue = "STUDENT")
                              Role role,
                              @NotNull
                              @Schema(defaultValue = "true")
                              Boolean isAcceptTerms) {
}
