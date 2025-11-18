package com.exam.examapp.repository.subject;

import com.exam.examapp.dto.response.subject.SubmoduleProjection;
import com.exam.examapp.model.exam.Module;
import com.exam.examapp.model.exam.Submodule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmoduleRepository extends JpaRepository<Submodule, UUID> {
    List<Submodule> getAllByModule_Id(UUID moduleId);

    Optional<Submodule> getByName(String name);

    boolean existsByNameAndModule(String name, Module module);

    @Query(value = """
                select
                    s.id,
                    s.name,
                    m.id,
                    m.name,
                    m.logo_url,
                    m.created_at,
                    m.updated_at,
                    s.logo_url,
                    s.created_at,
                    s.updated_at,
                    coalesce(string_agg(distinct subj.name, '!'), '') as subjectNames,
                    count(distinct ex.id) as examCount
                from sub_modules s
                left join subject_structures ss on ss.submodule_id = s.id
                left join subjects subj on subj.id = ss.subject_id
                left join subject_structures_questions ssq on ssq.subject_structure_id = ss.id
                left join exams_subject_structure_questions esq on esq.subject_structure_questions_id = ssq.id
                left join exams ex on ex.id = esq.exam_id
                left join modules m on s.module_id = m.id
                group by
                    s.id,
                    s.name,
                    s.logo_url,
                    s.created_at,
                    s.updated_at,
                    m.id,
                    m.name,
                    m.logo_url,
                    m.created_at,
                    m.updated_at
            """, nativeQuery = true)
    List<SubmoduleProjection> getAllNative();
}
