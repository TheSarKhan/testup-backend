package com.exam.examapp.repository.subject;

import com.exam.examapp.dto.response.subject.ModuleResponse;
import com.exam.examapp.model.exam.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    boolean existsModuleByName(String name);

    Optional<Module> getModuleByName(String name);

    @Query("""
                select new com.exam.examapp.dto.response.subject.ModuleResponse(
                    m,
                    count(distinct sm.id),
                    count(distinct ex.id)
                )
                from Module m
                left join Submodule sm on sm.module.id = m.id
                left join SubjectStructure ss on ss.submodule.id = sm.id
                left join SubjectStructureQuestion ssq on ssq.subjectStructure.id = ss.id
                left join Exam ex on ex.id IN (
                    SELECT ex2.id
                    FROM Exam ex2
                    JOIN ex2.subjectStructureQuestions s2
                    WHERE s2.id = ssq.id
                    )
                group by m
            """)
    List<ModuleResponse> getAllModuleResponses();
}
