package com.exam.examapp.service.impl.user;

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

    public Specification<User> hasNameOrEmailLike(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) return null;
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }

    public Specification<User> filter(List<String> packNames,
                                      List<Role> roles,
                                      Boolean isActive,
                                      Instant createAtAfter,
                                      Instant createAtBefore) {
        Specification<User> spec = Specification.unrestricted();

        if (packNames != null && !packNames.isEmpty())
            spec = spec.and(hasPackNames(packNames));

        if (roles != null && !roles.isEmpty())
            spec = spec.and(hasRoles(roles));

        if (isActive != null)
            spec = spec.and(hasActiveStatus(isActive));

        if (createAtAfter != null)
            spec = spec.and(createdAfter(createAtAfter));

        if (createAtBefore != null)
            spec = spec.and(createdBefore(createAtBefore));

        return spec;
    }

    public Specification<User> hasPackNames(List<String> packNames) {
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

    public Specification<User> hasRoles(List<Role> roles) {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) return null;
            return root.get("role").in(roles);
        };
    }

    public Specification<User> hasActiveStatus(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) return null;
            return cb.equal(root.get("isActive"), isActive);
        };
    }

    public Specification<User> createdAfter(Instant after) {
        return (root, query, cb) -> {
            if (after == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), after);
        };
    }

    public Specification<User> createdBefore(Instant before) {
        return (root, query, cb) -> {
            if (before == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdAt"), before);
        };
    }
}
