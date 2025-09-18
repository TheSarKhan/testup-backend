package com.exam.examapp.dto.response;

import com.exam.examapp.model.enums.Role;

public record ProfileSettingsResponse(String id,
                                      String username,
                                      String profilePictureUrl,
                                      Role role,
                                      String email,
                                      String phoneNumber) {
}
