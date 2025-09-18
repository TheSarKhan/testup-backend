package com.exam.examapp.dto.response;

import java.util.List;

public record HeaderResponse(
        List<NotificationResponse> notifications,
        String profilePictureUrl
) {}
