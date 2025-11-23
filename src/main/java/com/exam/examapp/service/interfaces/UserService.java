package com.exam.examapp.service.interfaces;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User save(User user);

    User getByEmail(String email);

    User getByPhoneNumber(String phoneNumber);

    User getCurrentUser();

    User getUserById(UUID userId);

    User getCurrentUserOrNull();

    List<User> getUsersByRole(Role role);

    List<String> getEmailList(List<String> packNames,
                              List<Role> roles,
                              Boolean isActive,
                              Instant createAtAfter,
                              Instant createAtBefore);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
