package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.UserFilterRequest;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;

import java.util.List;
import java.util.UUID;

public interface UserService {
  User save(User user);

  User getByEmail(String email);

  User getByPhoneNumber(String phoneNumber);

  User getCurrentUser();

  User getUserById(UUID userId);

  User getCurrentUserOrNull();

  TeacherInfo getTeacherInfo();

  List<User> getUsersByRole(Role role);

  List<String> getEmailList(UserFilterRequest request);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);
}
