package com.exam.examapp.model;

import com.exam.examapp.model.enums.Role;
import com.exam.examapp.security.model.CustomUserDetails;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "users")
@RequiredArgsConstructor
public class User {
  @Id private UUID id;

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

  @ManyToOne private Pack pack;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb")
  private TeacherInfo info;

  private boolean isAcceptedTerms;

  private boolean isActive;

  private boolean isDeleted;

  private boolean isEnabled;

  private boolean isAccountNonExpired;

  private boolean isAccountNonLocked;

  private boolean isCredentialsNonExpired;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @PrePersist
  void prePersist() {
    id = UUID.randomUUID();
    isActive =
        isAccountNonExpired = isAccountNonLocked = isCredentialsNonExpired = isEnabled = true;
    updatedAt = createdAt = LocalDateTime.now();
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public CustomUserDetails getCustomUserDetails() {
    return new CustomUserDetails(this);
  }
}
