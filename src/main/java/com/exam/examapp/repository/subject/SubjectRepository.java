package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    boolean existsSubjectByName(String name);

    Optional<Subject> getByName(String name);
}
