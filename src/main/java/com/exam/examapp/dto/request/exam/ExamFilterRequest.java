package com.exam.examapp.dto.request.exam;

import java.util.List;
import java.util.UUID;

public record ExamFilterRequest(
        String name,
        Integer minCost,
        Integer maxCost,
        List<Integer> rating,
        List<UUID> tagIds,
        Integer pageNum
) {
}
