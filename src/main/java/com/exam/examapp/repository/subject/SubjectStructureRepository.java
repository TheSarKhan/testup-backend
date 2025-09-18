package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.SubjectStructure;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectStructureRepository extends JpaRepository<SubjectStructure, UUID> {
  List<SubjectStructure> getBySubject_Id(UUID subjectId);

    List<SubjectStructure> getBySubmodule_Id(UUID submoduleId);
}
