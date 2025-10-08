package com.exam.examapp.repository;

import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentResultRepository extends JpaRepository<PaymentResult, UUID> {
    List<PaymentResult> getByUser(User user);

    @Query("from PaymentResult where user = :user and createdAt < :createdAtNeeded")
    List<PaymentResult> getByUserAndLastCreatedAt(User user, Instant createdAtNeeded);

    Optional<PaymentResult> getByInvoiceUuid(String uuid);

    List<PaymentResult> getByStatusAndCreatedAtAfter(String status, Instant createdAtAfter);

    List<PaymentResult> getByStatus(String status);
}
