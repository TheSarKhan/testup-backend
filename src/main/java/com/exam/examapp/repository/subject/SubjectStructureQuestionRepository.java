package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectStructureQuestionRepository
        extends JpaRepository<SubjectStructureQuestion, UUID> {
    @Query("select id from SubjectStructureQuestion where subjectStructure in (:subjectStructures)")
    List<UUID> getIdsBySubjectStructureIn(Collection<SubjectStructure> subjectStructures);
}
