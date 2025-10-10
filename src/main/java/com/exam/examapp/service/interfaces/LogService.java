package com.exam.examapp.service.interfaces;

import com.exam.examapp.model.Log;
import com.exam.examapp.model.User;

import java.util.List;
import java.util.UUID;

public interface LogService {
    void save(String message, User user);

    List<Log> getAllOrderByCreatedAt(int page, int size);

    Log getById(UUID id);

    void update(UUID id, String message);

    void delete(UUID id);
}
