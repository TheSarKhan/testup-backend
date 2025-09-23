package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.HeaderResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.service.interfaces.HeaderService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeaderServiceImpl implements HeaderService {
  private final NotificationService notificationService;

  private final UserService userService;

  @Override
  public HeaderResponse getHeaderInfo() {
      User user = userService.getCurrentUser();
      return new HeaderResponse(
        notificationService.getAllNotificationsSortedByCreatedAt(),
        user.getProfilePictureUrl(),
        user.getCurrentExams());
  }
}
