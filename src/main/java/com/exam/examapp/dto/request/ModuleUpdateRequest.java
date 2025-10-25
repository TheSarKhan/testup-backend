package com.exam.examapp.dto.request;

import java.util.UUID;

public record ModuleUpdateRequest(
        UUID id,
        String moduleName
) {
}
