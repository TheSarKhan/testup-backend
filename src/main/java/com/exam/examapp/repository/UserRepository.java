package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> getUserById(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> getAllByRole(Role role);

    List<User> getByRole(Role role);

    long countByCreatedAtAfter(Instant createdAtAfter);

    long countByCreatedAtBetween(Instant createdAtAfter, Instant createdAtBefore);

    long countByCreatedAtBetweenAndRole(Instant createdAtAfter, Instant createdAtBefore, Role role);

    List<User> getAllByEmailIn(Collection<String> emails);
}
