package com.exam.examapp.dto.response;

import java.time.Instant;
import java.util.UUID;

public record LogResponse(
        UUID id,
        String message,
        UUID userId,
        String username,
        String profilePictureUrl,
        String email,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
