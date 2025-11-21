package com.exam.examapp.service.impl;

import com.exam.examapp.dto.response.LogResponse;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Log;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.LogRepository;
import com.exam.examapp.service.interfaces.LogService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Override
    public void save(String message, User user) {
        logRepository.save(Log.builder().message(message).user(user).build());
    }

    @Override
    public List<LogResponse> getAllOrderByCreatedAtDesc(int page, int size) {
        int skip = (page - 1) * size;
        return logRepository.getAllOrderByCreatedAtDesc(skip, size).
                stream()
                .map(this::logToResponse)
                .toList();
    }

    @Override
    public List<LogResponse> getAllByFilter(Role role, List<String> filters, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Specification<Log> specification = Specification.unrestricted();
        specification.and(hasRole(role));
        specification.and(hasFilter(filters));

        log.info("Role :{} Filters :{} Page :{} Size :{}", role, filters, page, size);
        List<Log> logs = logRepository.findAll(specification, pageable).getContent();
        log.info("Logs size :{}", logs.size());
        log.info("Log :{}", logs.isEmpty() ? "empty" : logs.getFirst());
        return logs.stream().map(this::logToResponse).toList();
    }

    private Specification<Log> hasRole(Role role) {
        if (role == null) return null;
        return (root, query, cb) -> root.get("user").get("role").in(role);
    }

    private Specification<Log> hasFilter(List<String> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> orPredicates = new ArrayList<>();

            for (String filter : filters) {
                switch (filter) {
                    case "Qeydiyyatdan keçən şagirdlər" ->
                            orPredicates.add(cb.like(root.get("message"), "%Tələbə qeydiyyatdan keçdi%"));
                    case "Qeydiyyatdan keçən müəllimlər" ->
                            orPredicates.add(cb.like(root.get("message"), "%Müəllim qeydiyyatdan keçdi%"));
                    case "Aktiv edilən hesablar" -> orPredicates.add(cb.like(root.get("message"), "%aktiv edildi%"));
                    case "Deaktiv edilən hesablar" ->
                            orPredicates.add(cb.like(root.get("message"), "%deaktiv edildi%"));
                    case "Planı dəyişdirilən müəllimlər" ->
                            orPredicates.add(cb.like(root.get("message"), "%paketini dəyişdirdi%"));
                    case "Alınan planlar" -> orPredicates.add(cb.like(root.get("message"), "%paket aldı%"));
                    case "Yaradılan imtahanlar" ->
                            orPredicates.add(cb.like(root.get("message"), "%İmtahanın yaradılması tamamlandı%"));
                    case "Düzəliş edilən imtahanlar" ->
                            orPredicates.add(cb.like(root.get("message"), "%İmtahan yeniləndi%"));
                    case "Silinən imtahanlar" -> orPredicates.add(cb.like(root.get("message"), "%İmtahan silindi%"));
                    case "Alınan imtahanlar" -> orPredicates.add(cb.like(root.get("message"), "%Tələbə imtahan aldı%"));
                    case "Birləşmiş imtahana əlavə edilən müəllimlər" ->
                            orPredicates.add(cb.like(root.get("message"), "%Müəllim(lər) uğurla əlavə edildi%"));
                    case "Şagirdlərə əlavə olunan imtahanlar" ->
                            orPredicates.add(cb.like(root.get("message"), "%İmtahan sagirdə əlavə olundu%"));
                    default -> {
                    }
                }
            }

            if (orPredicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.or(orPredicates.toArray(new Predicate[0]));
        };
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
