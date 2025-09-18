package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.dto.request.NotificationUpdateRequest;
import com.exam.examapp.dto.response.NotificationResponse;
import com.exam.examapp.model.Notification;

public class NotificationMapper {
    public static Notification requestTo(NotificationRequest request){
        return Notification.builder()
                .title(request.title())
                .message(request.message())
                .build();
    }

    public static Notification updateRequestTo(Notification notification, NotificationUpdateRequest request){
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        return notification;
    }

    public static NotificationResponse toResponseForMyNotification(Notification notification){
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getUpdatedAt());
    }

    public static NotificationResponse toResponseForAdmin(Notification notification){
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getUser().getEmail(),
                notification.getCreatedAt(),
                notification.getUpdatedAt());
    }
}
