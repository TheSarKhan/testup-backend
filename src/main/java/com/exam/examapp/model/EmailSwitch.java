package com.exam.examapp.model;

import com.exam.examapp.model.enums.Email;
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
@Table(name = "email_switch")
public class EmailSwitch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Email email;

    private Instant updatedAt;

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
