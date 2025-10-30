package com.exam.examapp.model.information;

import com.exam.examapp.model.enums.ContactImageType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "contacts")
public class Contact {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "JSONB")
    private Map<String, String> nameToRedirectUrl;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "JSONB")
    private Map<String, Map<ContactImageType, String>> nameToImageUrls;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
        if (nameToRedirectUrl == null) {
            nameToRedirectUrl = Map.of();
        }
        if (nameToImageUrls == null) {
            nameToImageUrls = Map.of();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
