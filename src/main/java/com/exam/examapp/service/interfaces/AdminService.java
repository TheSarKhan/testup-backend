package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.model.enums.Role;

import java.util.UUID;

public interface AdminService {
    void changeUserRoleViaEmail(String email, Role role);

    void changeUserRoleViaId(UUID id, Role role);

    AdminStatisticsResponse getAdminStatistics();
}
