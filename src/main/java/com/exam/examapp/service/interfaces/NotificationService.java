package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.MultiNotificationRequest;
import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.dto.request.NotificationUpdateRequest;
import com.exam.examapp.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void sendNotification(NotificationRequest request);

    void sendNotificationToAll(MultiNotificationRequest request);

    List<NotificationResponse> getAllNotifications();

    List<NotificationResponse> getAllNotificationsSortedByCreatedAt();

    List<NotificationResponse> getMyNotifications();

    List<NotificationResponse> getNotificationsByEmail(String email);

    NotificationResponse getNotificationById(UUID id);

    void updateNotification(NotificationUpdateRequest request);

    void deleteNotification(UUID id);
}
