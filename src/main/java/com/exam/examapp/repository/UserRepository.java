package com.exam.examapp.repository;

import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> getAllByRole(Role role);

    long countByCreatedAtAfter(Instant createdAtAfter);

    long countByCreatedAtBetween(Instant createdAtAfter, Instant createdAtBefore);

    long countByCreatedAtBetweenAndRole(Instant createdAtAfter, Instant createdAtBefore, Role role);

    List<User> getAllByEmailIn(Collection<String> emails);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query(value = """
            SELECT *
            FROM users
            WHERE role = 'TEACHER'
              AND (info->>'thisMonthStartTime')::timestamptz
                    < (NOW() - INTERVAL '30 days')
            """, nativeQuery = true)
    List<User> getTeachersByLastMonth();

    @Query(value = """
            SELECT u.*
            FROM users u
            JOIN packs p ON p.id = u.pack_id
            WHERE u.role = 'TEACHER'
                AND p.pack_name <> :defaultPackName
                AND u.next_payment_date > (NOW() - INTERVAL '3 days' + INTERVAL '1 second')
                AND u.next_payment_date < (NOW() + INTERVAL '11 days' - INTERVAL '1 second')
            """, nativeQuery = true)
    List<User> getTeachersByPackExceptDefault(@Param("defaultPackName") String defaultPackName);

    @Query("select u.email from User u")
    List<String> findEmailsBySpecification(Specification<User> spec);
}
