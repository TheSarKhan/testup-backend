package com.exam.examapp.repository;

import com.exam.examapp.model.Notification;
import com.exam.examapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> getNotificationsByUser(User user);

    List<Notification> getNotificationsByUserAndIsRead(User user, boolean isRead);

    @Query("from Notification order by createdAt")
    List<Notification> getAllNotificationsSortedByCreatedAt();
}
