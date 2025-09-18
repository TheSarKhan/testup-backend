package com.exam.examapp.repository.information;

import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.model.information.Superiority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuperiorityRepository extends JpaRepository<Superiority, UUID> {
    List<Superiority> getSuperiorityByType(SuperiorityType type);
}
