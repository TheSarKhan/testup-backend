package com.exam.examapp.repository;

import com.exam.examapp.model.EmailSwitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailSwitchRepository extends JpaRepository<EmailSwitch, UUID> {
    @Query("from EmailSwitch")
    Optional<EmailSwitch> get();
}
