package com.exam.examapp.dto.response.subject;

import com.exam.examapp.model.exam.Module;

public record ModuleResponse(
        Module module,
        long submoduleCount,
        long examCount
) {
}
