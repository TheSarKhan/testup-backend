package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.SubjectStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectStructureRepository extends JpaRepository<SubjectStructure, UUID> {
    List<SubjectStructure> getBySubject_Id(UUID subjectId);

    List<SubjectStructure> getBySubmodule_Id(UUID submoduleId);

    Optional<SubjectStructure> getBySubmodule_IdAndSubject_Id(UUID submoduleId, UUID subjectId);

    boolean existsBySubmodule_IdAndSubject_Id(UUID submoduleId, UUID subjectId);
}
