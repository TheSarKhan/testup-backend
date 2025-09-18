package com.exam.examapp.repository.subject;

import com.exam.examapp.model.subject.SubjectStructureQuestion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectStructureQuestionRepository
    extends JpaRepository<SubjectStructureQuestion, UUID> {}
