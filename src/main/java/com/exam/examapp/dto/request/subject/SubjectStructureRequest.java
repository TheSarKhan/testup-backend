package com.exam.examapp.dto.request.subject;

import com.exam.examapp.model.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SubjectStructureRequest(@NotNull UUID subjectId, UUID submoduleId,
                                      @Schema(defaultValue = "10") int questionCount,
                                      Map<QuestionType, List<Integer>> questionTypeCountMap,
                                      Map<QuestionType, Map<Integer, Map<QuestionType, List<Integer>>>> textListeningQuestionToCountMap,
                                      Map<Integer, Integer> questionToPointMap, String formula) {
}
