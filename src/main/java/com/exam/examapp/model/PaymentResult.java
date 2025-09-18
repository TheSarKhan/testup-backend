package com.exam.examapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "payment_results")
public class PaymentResult {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    private UUID productId;

    private String uuid;

    private Double amount;

    private String currency;

    private String status;

    private String description;

    private String paymentDay;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
