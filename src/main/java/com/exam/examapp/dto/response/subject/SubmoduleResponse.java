package com.exam.examapp.dto.response.subject;

import com.exam.examapp.model.exam.Submodule;

import java.util.List;

public record SubmoduleResponse(
        Submodule submodule,
        List<String> subjectNames,
        long ExamCount
) {
}
