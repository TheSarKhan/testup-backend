package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.response.LogResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;

import java.util.List;
import java.util.UUID;

public interface LogService {
    void save(String message, User user);

    List<LogResponse> getAllOrderByCreatedAt(int page, int size);

    List<LogResponse> getAllByFilter(Role role, List<String> filters, int page, int size);

    LogResponse getById(UUID id);

    void update(UUID id, String message);

    void delete(UUID id);
}
