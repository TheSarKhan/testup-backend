package com.exam.examapp.dto.request;

import java.time.Instant;
import java.util.List;

public record TeacherFilter(
        List<String> packNames,
        Boolean isActive,
        Instant createAtAfter,
        Instant createAtBefore,
        int page,
        int size
) {
}
