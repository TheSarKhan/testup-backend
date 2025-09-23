package com.exam.examapp.service.impl;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.service.interfaces.AdminService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserService userService;

    @Override
    public void changeUserRoleViaEmail(String email, Role role) {
        User user = userService.getByEmail(email);
        changeUserRole(user, role);
    }

    @Override
    public void changeUserRoleViaId(UUID id, Role role) {
        User user = userService.getUserById(id);
        changeUserRole(user, role);
    }

    private void changeUserRole(User user, Role role) {
        user.setRole(role);
        userService.save(user);
    }
}
