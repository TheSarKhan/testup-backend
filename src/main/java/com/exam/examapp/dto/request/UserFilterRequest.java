package com.exam.examapp.dto.request;

import com.exam.examapp.model.enums.Role;

import java.time.Instant;
import java.util.List;

public record UserFilterRequest(
        List<String> packNames,
        List<Role> roles,
        Boolean isActive,
        Instant createAtAfter,
        Instant createAtBefore
) {
}
