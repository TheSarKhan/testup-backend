package com.exam.examapp.repository;

import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            select COALESCE(sum(amount))
            from PaymentResult
            where createdAt > :createdAtAfter
                and createdAt < :createdAtBefore
            """)
    Double getAmountPaidByRange(Instant createdAtAfter, Instant createdAtBefore);

    @Query("""
            select coalesce(sum(amount), 0)
            from PaymentResult
            where status = :status
            """)
    double sumAmountsByStatus(@Param("status") String status);

    @Query(value = """
            select coalesce(sum(amount), 0)
            from PaymentResult
            WHERE status = :status
              AND createdAt > :afterDate
            """)
    double sumApprovedPaymentsAfter(
            @Param("status") String status,
            @Param("afterDate") Instant afterDate);
}
