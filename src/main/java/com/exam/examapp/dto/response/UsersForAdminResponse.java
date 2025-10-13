package com.exam.examapp.dto.response;

import com.exam.examapp.model.enums.Role;

import java.time.Instant;
import java.util.UUID;

public record UsersForAdminResponse(
        UUID id,
        String profilePictureUrl,
        String email,
        String fullName,
        Role role,
        String packName,
        String phoneNumber,
        Instant createAt,
        boolean isActive
) {
}
