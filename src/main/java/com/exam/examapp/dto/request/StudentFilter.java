package com.exam.examapp.dto.request;

import java.time.Instant;

public record StudentFilter(
        Boolean isActive,
        Instant createAtAfter,
        Instant createAtBefore,
        int page,
        int size
) {
}
