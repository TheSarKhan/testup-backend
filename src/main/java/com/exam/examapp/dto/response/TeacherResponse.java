package com.exam.examapp.dto.response;

import com.exam.examapp.model.subject.Subject;

import java.util.List;
import java.util.UUID;

public record TeacherResponse(
        UUID teacherId,
        String fullName,
        String profilePictureUrl,
        String email,
        List<Subject> subjects
) {
}
