package com.exam.examapp.repository;

import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentResultRepository extends JpaRepository<PaymentResult, UUID> {
    List<PaymentResult> getByUser(User user);

    Optional<PaymentResult> getByUuid(String uuid);
}
