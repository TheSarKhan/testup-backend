package com.exam.examapp.dto.response.exam;

import java.util.List;

public record ExamAllResponses(
        List<ExamBlockResponse> examBlocks,
        Integer totalExamCount
) {
}
