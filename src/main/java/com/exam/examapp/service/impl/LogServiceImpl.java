package com.exam.examapp.service.impl;

import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Log;
import com.exam.examapp.model.User;
import com.exam.examapp.repository.LogRepository;
import com.exam.examapp.service.interfaces.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Override
    public void save(String message, User user) {
        logRepository.save(Log.builder().message(message).user(user).build());
    }

    @Override
    public List<Log> getAllOrderByCreatedAt(int page, int size) {
        int skip = (page - 1) * size;
        return logRepository.getAllOrderByCreatedAt(skip, size);
    }

    @Override
    public Log getById(UUID id) {
        return logRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Log not found"));
    }

    @Override
    public void update(UUID id, String message) {
        Log log = getById(id);
        log.setMessage(message);
        logRepository.save(log);
    }

    @Override
    public void delete(UUID id) {
        Log log = getById(id);
        log.setDeletedAt(Instant.now());
        logRepository.save(log);
    }
}
