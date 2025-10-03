package com.exam.examapp.model;

import com.exam.examapp.dto.CurrentExam;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.security.model.CustomUserDetails;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "users")
@RequiredArgsConstructor
public class User {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String password;

    private String profilePictureUrl;

    private String googleId;

    @ManyToOne
    private Pack pack;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private TeacherInfo info;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<CurrentExam> currentExams;

    private boolean isAcceptedTerms;

    private boolean isActive;

    private boolean isDeleted;

    private boolean isEnabled;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private Instant nextPaymentDate;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        isActive =
                isAccountNonExpired = isAccountNonLocked = isCredentialsNonExpired = isEnabled = true;
        updatedAt = createdAt = Instant.now();
        currentExams = List.of();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
