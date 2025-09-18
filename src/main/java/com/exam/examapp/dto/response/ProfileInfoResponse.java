package com.exam.examapp.dto.response;

import com.exam.examapp.model.enums.Role;

public record ProfileInfoResponse(String id,
                                  String username,
                                  String profilePictureUrl,
                                  Role role) {
}
