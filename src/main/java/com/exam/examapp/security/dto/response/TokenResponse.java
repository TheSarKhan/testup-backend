package com.exam.examapp.security.dto.response;

import com.exam.examapp.model.Pack;
import com.exam.examapp.model.enums.Role;

public record TokenResponse(String accessToken,
                            String refreshToken,
                            Role role,
                            Pack pack) {
}
