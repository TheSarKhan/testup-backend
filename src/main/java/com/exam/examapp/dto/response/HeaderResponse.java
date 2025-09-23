package com.exam.examapp.dto.response;

import com.exam.examapp.dto.CurrentExam;

import java.util.List;

public record HeaderResponse(
        List<NotificationResponse> notifications,
        String profilePictureUrl,
        List<CurrentExam> currentExams
) {}
