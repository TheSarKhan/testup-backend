package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.LogResponse;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Log;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.LogRepository;
import com.exam.examapp.service.interfaces.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public List<LogResponse> getAllOrderByCreatedAt(int page, int size) {
        int skip = (page - 1) * size;
        return logRepository.getAllOrderByCreatedAt(skip, size).
                stream()
                .map(this::logToResponse)
                .toList();
    }

    @Override
    public List<LogResponse> getAllByFilter(List<Role> roles, List<String> filters, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Log> specification = Specification.unrestricted();
        specification.and(hasRole(roles));
        for (String filter : filters) addSpec(filter);

        return logRepository.findAll(specification, pageable)
                .stream().map(this::logToResponse).toList();
    }

    private Specification<Log> hasRole(List<Role> roles) {
        if (roles == null || roles.isEmpty()) return null;
        return (root, query, cb) -> root.get("user").get("role").in(roles);
    }

    private Specification<Log> addSpec(String filter) {
        Specification<Log> specification = Specification.unrestricted();
        switch (filter){
            case "" -> {}
            default -> {}
        }

        return specification;
    }

    @Override
    public LogResponse getById(UUID id) {
        return logToResponse(getLogById(id));
    }

    @Override
    public void update(UUID id, String message) {
        Log log = getLogById(id);
        log.setMessage(message);
        logRepository.save(log);
    }

    @Override
    public void delete(UUID id) {
        Log log = getLogById(id);
        log.setDeletedAt(Instant.now());
        logRepository.save(log);
    }

    private Log getLogById(UUID id) {
        return logRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Log not found"));
    }

    private LogResponse logToResponse(Log log) {
        User user = log.getUser();
        if (user == null)
            return new LogResponse(
                    log.getId(),
                    log.getMessage(),
                    null,
                    null,
                    null,
                    null,
                    log.getDeletedAt(),
                    log.getCreatedAt(),
                    log.getUpdatedAt()
            );
        return new LogResponse(
                log.getId(),
                log.getMessage(),
                user.getId(),
                user.getFullName(),
                user.getProfilePictureUrl(),
                user.getEmail(),
                log.getDeletedAt(),
                log.getCreatedAt(),
                log.getUpdatedAt());
    }
}
