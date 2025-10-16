package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.SubjectStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectStructureRepository extends JpaRepository<SubjectStructure, UUID> {
    boolean existsBySubmodule_IdAndSubject_IdAndIsActive(UUID submoduleId, UUID subjectId, boolean isActive);

    List<SubjectStructure> findByIsActive(boolean isActive);

    List<SubjectStructure> getBySubject_IdAndIsActive(UUID subjectId, boolean isActive);

    List<SubjectStructure> getBySubmodule_IdAndIsActive(UUID submoduleId, boolean isActive);

    Optional<SubjectStructure> getBySubmodule_IdAndSubject_IdAndIsActive(UUID submoduleId, UUID subjectId, boolean isActive);

    @Query("from SubjectStructure where submodule is not null and isActive = :isActive")
    List<SubjectStructure> getBySubmoduleIsNotNullAndIsActive(boolean isActive);
}
