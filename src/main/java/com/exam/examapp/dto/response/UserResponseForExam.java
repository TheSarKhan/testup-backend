package com.exam.examapp.dto.response;

import com.exam.examapp.model.enums.Role;
import java.util.UUID;

public record UserResponseForExam(UUID id, String email, String profilePictureUrl, Role role) {}
