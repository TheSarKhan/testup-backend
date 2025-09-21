package com.exam.examapp.dto.request.exam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AddExamTeacherRequest(UUID examId,
                                    Map<String, List<UUID>> teacherEmailToSubjectIds) {
}
