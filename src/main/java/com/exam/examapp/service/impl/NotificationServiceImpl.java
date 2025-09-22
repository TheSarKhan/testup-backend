package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.dto.request.NotificationUpdateRequest;
import com.exam.examapp.dto.response.NotificationResponse;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.NotificationMapper;
import com.exam.examapp.model.Notification;
import com.exam.examapp.model.User;
import com.exam.examapp.repository.NotificationRepository;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    private final UserService userService;

    @Override
    public void sendNotification(NotificationRequest request) {
        Notification notification = NotificationMapper.requestTo(request);
        notification.setUser(userService.getByEmail(request.email()));
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationMapper::toResponseForAdmin)
                .toList();
    }

    @Override
    public List<NotificationResponse> getAllNotificationsSortedByCreatedAt() {
        User user = userService.getCurrentUser();
        return notificationRepository.getAllNotificationsSortedByCreatedAt(user)
                .stream()
                .map(NotificationMapper::toResponseForMyNotification)
                .toList();
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {
        User user = userService.getCurrentUser();
        return notificationRepository.getNotificationsByUser(user)
                .stream()
                .map(NotificationMapper::toResponseForMyNotification)
                .toList();
    }

    @Override
    @Transactional
    public List<NotificationResponse> getNotificationsByEmail(String email) {
        User user = userService.getByEmail(email);
        return notificationRepository.getNotificationsByUser(user)
                .stream()
                .map(NotificationMapper::toResponseForAdmin)
                .toList();
    }

    @Override
    public NotificationResponse getNotificationById(UUID id) {
        Notification byId = getById(id);
        byId.setRead(true);
        notificationRepository.save(byId);
        return NotificationMapper.toResponseForMyNotification(byId);
    }

    @Override
    public void updateNotification(NotificationUpdateRequest request) {
        Notification updatedNotification = NotificationMapper
                .updateRequestTo(getById(request.id()), request);
        updatedNotification.setRead(false);
        updatedNotification.setUser(userService.getByEmail(request.email()));
        notificationRepository.save(updatedNotification);
    }

    @Override
    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }

    private Notification getById(UUID id) {
        return notificationRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Notification not found."));
    }
}
