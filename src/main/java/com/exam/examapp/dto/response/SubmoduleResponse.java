package com.exam.examapp.dto.response;

import com.exam.examapp.model.exam.Submodule;

import java.util.List;

public record SubmoduleResponse(
        Submodule submodule,
        List<String> subjectNames,
        long ExamCount
) {
}
