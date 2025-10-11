package com.exam.examapp.service.impl.user;

import com.exam.examapp.dto.request.UserFilterRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Pack;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.PackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSpecification {
    private final PackRepository packRepository;

    public Specification<User> filter(UserFilterRequest filter) {
        Specification<User> specification = Specification.unrestricted();
        return specification
                .and(hasPackNames(filter.packNames()))
                .and(hasRoles(filter.roles()))
                .and(hasActiveStatus(filter.isActive()))
                .and(createdAfter(filter.createAtAfter()))
                .and(createdBefore(filter.createAtBefore()));
    }

    private Specification<User> hasPackNames(List<String> packNames) {
        return (root, query, cb) -> {
            if (packNames == null || packNames.isEmpty()) return null;

            List<Pack> packs = new ArrayList<>();
            for (String name : packNames) {
                Pack pack = packRepository.getPackByPackName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Paket tapılmadı: " + name));
                packs.add(pack);
            }
            return root.get("pack").in(packs);
        };
    }

    private Specification<User> hasRoles(List<Role> roles) {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) return null;
            return root.get("role").in(roles);
        };
    }

    private Specification<User> hasActiveStatus(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) return null;
            return cb.equal(root.get("isActive"), isActive);
        };
    }

    private Specification<User> createdAfter(Instant after) {
        return (root, query, cb) -> {
            if (after == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), after);
        };
    }

    private Specification<User> createdBefore(Instant before) {
        return (root, query, cb) -> {
            if (before == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdAt"), before);
        };
    }
}
