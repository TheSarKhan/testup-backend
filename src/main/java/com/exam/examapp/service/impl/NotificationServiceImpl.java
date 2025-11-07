package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.MultiNotificationRequest;
import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.dto.request.NotificationUpdateRequest;
import com.exam.examapp.dto.response.NotificationResponse;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.NotificationMapper;
import com.exam.examapp.model.Notification;
import com.exam.examapp.model.User;
import com.exam.examapp.repository.NotificationRepository;
import com.exam.examapp.repository.UserRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final LogService logService;

    @Override
    public void sendNotification(NotificationRequest request) {
        log.info("Bildiriş göndərilir");
        Notification notification = NotificationMapper.requestTo(request);
        notification.setUser(userService.getByEmail(request.email()));
        notificationRepository.save(notification);
        String message = "Bildiriş göndərildi. Message:" + request.message() + " mail: " + request.email();
        log.info(message);
        logService.save(message, userService.getCurrentUserOrNull());
    }

    @Override
    public void sendNotificationToAll(MultiNotificationRequest request) {
        log.info("Bildirişlər göndərilir");
        List<Notification> list = new ArrayList<>();
        List<User> getAllByEmail = userRepository.getAllByEmailIn(request.emails());
        for (User user : getAllByEmail) {
            list.add(Notification.builder()
                    .title(request.title())
                    .message(request.message())
                    .user(user)
                    .build());
        }
        notificationRepository.saveAll(list);
        String message = "Bildirişlər göndərildi. Message:" + request.message() + " mails: " + request.emails();
        log.info(message);
        logService.save(message, userService.getCurrentUserOrNull());
    }

    @Override
    @Transactional
    public List<NotificationResponse> getAllNotifications(int size, int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum-1, size);
        return notificationRepository.findAll(pageRequest)
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
        log.info("Bildiriş yenilənir");
        Notification updatedNotification = NotificationMapper
                .updateRequestTo(getById(request.id()), request);
        updatedNotification.setRead(false);
        updatedNotification.setUser(userService.getByEmail(request.email()));
        notificationRepository.save(updatedNotification);
        String message = "Bildiriş yeniləndi. Message" + request.message() + " mail: " + request.email();
        log.info(message);
        logService.save(message, userService.getCurrentUserOrNull());
    }

    @Override
    public void deleteNotification(UUID id) {
        log.info("Bildiriş silinir");
        notificationRepository.deleteById(id);
        log.info("Bildiriş silindi");
        logService.save("Bildiriş silindi", userService.getCurrentUserOrNull());
    }

    private Notification getById(UUID id) {
        return notificationRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Bildiriş tapılmadı"));
    }
}
