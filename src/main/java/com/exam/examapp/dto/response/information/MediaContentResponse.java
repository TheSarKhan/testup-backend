package com.exam.examapp.dto.response.information;

import com.exam.examapp.model.enums.PageType;
import java.time.Instant;
import java.util.UUID;

public record MediaContentResponse(
        UUID id,
        String text,
        String pictureUrl,
        String author,
        String backgroundColor,
        String textColor,
        PageType pageType,
        Instant createAt,
        Instant updatedAt
) {
}
